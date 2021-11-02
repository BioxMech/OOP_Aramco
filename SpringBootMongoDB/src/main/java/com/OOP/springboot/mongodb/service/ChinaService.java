package com.OOP.springboot.mongodb.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.China;
import com.OOP.springboot.mongodb.model.s3;
import com.OOP.springboot.mongodb.repository.ChinaRepository;
import com.OOP.springboot.mongodb.service.utils.ChinaExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ChinaService {

    @Autowired
    ChinaRepository repo;

    @Autowired
    s3Service s3Service;

    public China saveChina(China china) { return repo.save(china); }

    public List<China> saveListChina(List<Map<String, String>> china) {
        List<China> writeData = new ArrayList<>();
        for (Map<String, String> data: china) {
            writeData.add(new China(data));
        }
        return repo.saveAll(writeData);
    }

    public List<China> retrieveAllChina(){
        return repo.findAll();
    }

    public List<China> retrieveAllChinaByYear(String year){
        return repo.findByYear(year);
    }

    public List<China> retrieveAllChinaByYearAndCommodity(String year, String commodity){
        return repo.findByYearAndCommodity(year, commodity, Sort.by(Sort.Direction.ASC, "month"));
    }

    public List<China> retrieveAllChinaByYearAndTypeAndCommodity(String year, String type, String commodity){
        return repo.findByYearAndTypeAndCommodity(year, type, commodity);
    }

    public List<String> getLatestYearMonth(){
        List<China> latestEntry = repo.findFirstByOrderByYearDescMonthDesc();
        if (latestEntry.size() < 1) {
            return null;
        }
        List<String> latestYearMonth = new ArrayList<>();
        latestYearMonth.add(latestEntry.get(0).getYear());
        latestYearMonth.add(latestEntry.get(0).getMonth());
        return latestYearMonth;
    }

    public List<String> getAllDistinctCommodities(){
        return repo.findDistinctCommodities();
    }

    public List<String> getAllDistinctYears(){
        return repo.findDistinctYears();
    }

    public void saveAllExcelFiles(){

        // Retrieve all commodities and loop through them
        List<String> distinctCommodities = getAllDistinctCommodities();



        for (String commodity: distinctCommodities){

            // Create ArrayList to pass to ChinaExcel to save data
            ArrayList<List<China>> listByYear = new ArrayList<>();


            // In each iteration --> Loop through the years that we want
            for (int i=2018; i < Integer.parseInt(getLatestYearMonth().get(0)) + 1; i++){

                // Kerosene --> Special case. Has both imports and exports. Hence, have to call a separate method in ChinaExcel.

//                    System.out.println(i);

                // Retrieve data for each year and add it to the listByYear
                List<China> commodityByYear = retrieveAllChinaByYearAndCommodity(String.valueOf(i), commodity);
                listByYear.add(commodityByYear);

            }



            if (!(commodity.equals("Kerosene"))){
                System.out.println(commodity);
                // Each excel file is for ONE commodity
                ChinaExcel excel = new ChinaExcel(listByYear, commodity);
                excel.saveAllByYear();

            } else {
                ChinaExcel excel = new ChinaExcel(listByYear, commodity);
                excel.saveKerosene();
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String dateFormatted = df.format(new Date());

            // To save the object with link to the database
            if (commodity.indexOf(" ") != -1){
                String replacedCommodity = commodity.replace(" ", "+");
                s3 news3 = new s3(replacedCommodity, "China", dateFormatted + "/China/" + replacedCommodity + ".csv", dateFormatted);
                s3Service.saves3(news3);
            }
            else {
                s3 news3 = new s3(commodity, "China", dateFormatted + "/China/" + commodity + ".csv", dateFormatted);
                s3Service.saves3(news3);
            }


        }








    }

    public void deleteAll() { repo.deleteAll(); }
}

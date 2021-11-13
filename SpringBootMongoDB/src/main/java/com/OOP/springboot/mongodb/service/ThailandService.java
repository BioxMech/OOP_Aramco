package com.OOP.springboot.mongodb.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.China;
import com.OOP.springboot.mongodb.model.Thailand;
import com.OOP.springboot.mongodb.model.s3;
import com.OOP.springboot.mongodb.repository.ThailandRepository;
import com.OOP.springboot.mongodb.service.utils.ThailandExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThailandService {
    private final String[] crudeOilTypes = {"import", "production"};
    private final  String[] condensateTypes = {"production"};
    private final String[] petroleumProductsTypes = {"import", "production", "sales", "export", "net export"};
    private final String[] commodityParents = {"Crude Oil", "Condensate","Gasoline", "Kerosene", "Diesel", "JP", "Fuel Oil", "LPG", "Total"};
    private final String[] petroleumProductsParents = {"Gasoline", "Kerosene", "Diesel", "JP", "Fuel Oil", "LPG", "Total"};
    ArrayList<List<Thailand>> listByYear = null;

    @Autowired
    ThailandRepository repo;

    @Autowired
    s3Service s3Service;

    public Thailand saveThailand(Thailand thailand) {
        return repo.save(thailand);
    }

    public List<Thailand> saveListThailand(List<Map<String, String>> thailand) {
        List<Thailand> writeData = new ArrayList<>();
        for (Map<String, String> data: thailand) {
            String commodity = data.get("commodity");
            String type = data.get("type");
            if (commodity.equals("Condensate") | commodity.equals("Crude Oil")) {
                if (type.equals("production")) {
                    List<Thailand> searchResult = this.retrieveAllThailandByYearAndMonthAndTypeAndCommodityAndRegion(data.get("year"), data.get("month"), type, commodity, data.get("region"));
                    if (searchResult.size() == 0) {
                        writeData.add(new Thailand(data));
                    }
                }
                if (type.equals("import")) {
                    List<Thailand> searchResult = this.retrieveAllThailandByYearAndMonthAndTypeAndCommodityAndContinent(data.get("year"), data.get("month"), type, commodity, data.get("continent"));
                    if (searchResult.size() == 0) {
                        writeData.add(new Thailand(data));
                    }
                }

                if (type.equals("export")) {
                    List<Thailand> searchResult = this.retrieveAllThailandByYearAndMonthAndTypeAndCommodity(data.get("year"), data.get("month"), type, commodity);
                    if (searchResult.size() == 0) {
                        writeData.add(new Thailand(data));
                    }
                }
            } else if (commodity.equals("Material")) {
                List<Thailand> searchResult = this.retrieveAllThailandByYearAndMonthAndRefinery(data.get("year"), data.get("month"), data.get("refinery"));
                if (searchResult.size() == 0) {
                    writeData.add(new Thailand(data));
                }
            } else {
                List<Thailand> searchResult = this.retrieveAllThailandByYearAndMonthAndTypeAndCommodity(data.get("year"), data.get("month"), type, commodity);
                if (searchResult.size() == 0) {
                    writeData.add(new Thailand(data));
                }
            }

        }
        return repo.saveAll(writeData);
    }

    public List<Thailand> retrieveAllThailand() {
        return repo.findAll();
    }

    public List<Thailand> retrieveAllThailandByYear(String year) {
        return repo.findByYear(year);
    }

    public List<Thailand> retrieveAllThailandByYearAndCommodity(String year, String commodity) {
        return repo.findByYearAndCommodity(year, commodity);
    }

    public List<Thailand> retrieveAllThailandByYearAndTypeAndCommodity(String year, String type, String commodity) {
        return repo.findByYearAndTypeAndCommodity(year, type, commodity);
    }

    public List<Thailand> retrieveAllThailandByYearAndMonthAndTypeAndCommodity(String year, String month, String type, String commodity) {
        return repo.findByYearAndMonthAndTypeAndCommodity(year, month, type, commodity);
    }

    public List<Thailand> retrieveAllThailandByYearAndMonthAndRefinery(String year, String month, String refinery) {
        return repo.findByYearAndMonthAndRefinery(year, month, refinery);
    }

    public List<Thailand> retrieveAllThailandByYearAndRefinery(String year, String refinery) {
        return repo.findByYearAndRefinery(year, refinery);
    }

    public List<Thailand> retrieveAllThailandByYearAndMonthAndTypeAndCommodityAndRegion(String year, String month, String type, String commodity, String region) {
        return repo.findByYearAndMonthAndTypeAndCommodityAndRegion(year, month, type, commodity, region);
    }

    public List<Thailand> retrieveAllThailandByYearAndMonthAndTypeAndCommodityAndContinent(String year, String month, String type, String commodity, String continent) {
        return repo.findByYearAndMonthAndTypeAndCommodityAndContinent(year, month, type, commodity, continent);
    }

    public List<String> getAllDistinctCommodities() { return repo.findDistinctCommodities();}

    public List<String> getSubCommoditiesByParent(String parent) {
        List<String> result = new ArrayList<>();
        List<String> allCommodities = getAllDistinctCommodities();
        for (String ele: allCommodities) {
            if (ele.contains(parent)) {
                result.add(ele);
            }
        }
        return result;
    }

    public List<String> getAllDistinctRegions() { return repo.findDistinctRegions();}

    public List<String> getAllDistinctContinents() { return repo.findDistinctContinents();}

    public List<String> getAllDistinctRefineries() { return repo.findDistinctRefineries();}

    public List<String> getAllDistinctYears() { return repo.findDistinctYears();}

    public List<String> getLatestYear() {
        List<Thailand> latestEntry = repo.findFirstByOrderByYearDescMonthDesc();
        if (latestEntry.size() < 1) {
            return null;
        }
        List<String> latestYear = new ArrayList<>();
        latestYear.add(latestEntry.get(0).getYear());
        return latestYear;
    }

    private String reformatType(String type) {
        String[] typeSplit = type.split(" ");
        for (int i = 0; i < typeSplit.length; i++) {
            String reformatted = typeSplit[i].substring(0,1).toUpperCase() + typeSplit[i].substring(1);
            typeSplit[i] = reformatted;
        }
        String reformattedType = String.join("",typeSplit);
        return reformattedType;
    }

    public void saveExcelFilesByType() {
        List<String> distinctCommodities = getAllDistinctCommodities();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormatted = df.format(new Date());

        for (String commodity: commodityParents) {
//            System.out.println(Integer.parseInt(getLatestYear().get(0)) + 1);

            if (commodity.equals("Crude Oil")) {
                for (String type: crudeOilTypes) {
                    listByYear = new ArrayList<>();
                    String reformattedType = reformatType(type);
                    for (int i = 2017; i < Integer.parseInt(getLatestYear().get(0)) + 1; i++) {
//                        System.out.println(i);
                        List<Thailand> commodityByYear = retrieveAllThailandByYearAndTypeAndCommodity(String.valueOf(i), type,  commodity);
                        listByYear.add(commodityByYear);
                    }
                    ThailandExcel excel = new ThailandExcel(listByYear, reformattedType, commodity);
                    excel.saveAll();
                    String fileName =  String.join("",commodity.split(" ")) + reformattedType;
                    String replacedCommodity = commodity.replace(" ", "+");
                    s3 news3 = new s3(replacedCommodity, "Thailand", dateFormatted + "/Thailand/" + replacedCommodity + "/" + fileName + ".csv", dateFormatted);
                    s3Service.saves3(news3);
                }
            }
            else if (commodity.equals("Condensate")) {
                String type = "production";
                String reformattedType = reformatType(type);
                listByYear = new ArrayList<>();
                for (int i = 2017; i < Integer.parseInt(getLatestYear().get(0)) + 1; i++) {
                    List<Thailand> commodityByYear = retrieveAllThailandByYearAndTypeAndCommodity(String.valueOf(i), type,  commodity);
                    listByYear.add(commodityByYear);
                }
                ThailandExcel excel = new ThailandExcel(listByYear, reformattedType, commodity);
                excel.saveAll();
                String fileName =  String.join("",commodity.split(" ")) + reformattedType;
                String replacedCommodity = commodity.replace(" ", "+");
                s3 news3 = new s3(replacedCommodity, "Thailand", dateFormatted + "/Thailand/" + replacedCommodity + "/" + fileName + ".csv", dateFormatted);
                s3Service.saves3(news3);
            }
            else {
                for (String type: petroleumProductsTypes) {
                    /* reformattedType is NetExport, Sales, Production etc.*/
                    String reformattedType = reformatType(type);
                    listByYear = new ArrayList<>();
                    for (int i = 2017; i < Integer.parseInt(getLatestYear().get(0)) + 1; i++) {
                        List<Thailand> commodityByYear = retrieveAllThailandByYearAndTypeAndCommodity(String.valueOf(i), type,  commodity);
                        listByYear.add(commodityByYear);
                    }
                    ThailandExcel excel = new ThailandExcel(listByYear, reformattedType, commodity);
                    excel.saveAll();
                    String fileName =  String.join("",commodity.split(" ")) + reformattedType;
                    String replacedCommodity = commodity.replace(" ", "+");
                    s3 news3 = new s3(replacedCommodity, "Thailand", dateFormatted + "/Thailand/" + "Petroleum+Products" + "/" + fileName + ".csv", dateFormatted);
                    s3Service.saves3(news3);
                }
            }
        }
    }



    public void deleteAll() {
        repo.deleteAll();
    }
}

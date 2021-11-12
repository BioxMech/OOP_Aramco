package com.OOP.springboot.mongodb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.China;
import com.OOP.springboot.mongodb.model.Thailand;
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

//    public void saveAllExcelFiles() {
//        List<String> distinctCommodities = getAllDistinctCommodities();
//        for (String commodity:distinctCommodities) {
//            ArrayList<List<Thailand>> listByYear = new ArrayList<>();
//
//            for (int i = 2017; i < Integer.parseInt(getLatestYear().get(0)) + 1; i++) {
//                List<Thailand> commodityByYear = retrieveAllThailandByYearAndCommodity(String.valueOf(i),  commodity);
//                listByYear.add(commodityByYear);
//            }
//            ThailandExcel excel = new ThailandExcel(listByYear, commodity);
//            excel.saveAllByYear();
//        }
//    }

    public void saveExcelFilesByType() {
        List<String> distinctCommodities = getAllDistinctCommodities();
        for (String commodity: commodityParents) {
//            System.out.println(Integer.parseInt(getLatestYear().get(0)) + 1);

            if (commodity.equals("Crude Oil")) {
                for (String type: crudeOilTypes) {
                    listByYear = new ArrayList<>();
                    for (int i = 2017; i < Integer.parseInt(getLatestYear().get(0)) + 1; i++) {
//                        System.out.println(i);
                        List<Thailand> commodityByYear = retrieveAllThailandByYearAndTypeAndCommodity(String.valueOf(i), type,  commodity);
                        listByYear.add(commodityByYear);
                    }
                    ThailandExcel excel = new ThailandExcel(listByYear, type, commodity);
                    excel.saveAll();
                }
            }
            else if (commodity.equals("Condensate")) {
                String type = "production";
                listByYear = new ArrayList<>();
                for (int i = 2017; i < Integer.parseInt(getLatestYear().get(0)) + 1; i++) {
                    List<Thailand> commodityByYear = retrieveAllThailandByYearAndTypeAndCommodity(String.valueOf(i), type,  commodity);
                    listByYear.add(commodityByYear);
                }
                ThailandExcel excel = new ThailandExcel(listByYear, type, commodity);
                excel.saveAll();
            }
            else {
//                System.out.println(commodity);
                for (String type: petroleumProductsTypes) {
                    listByYear = new ArrayList<>();
                    for (int i = 2017; i < Integer.parseInt(getLatestYear().get(0)) + 1; i++) {
//                        System.out.println(i);
                        List<Thailand> commodityByYear = retrieveAllThailandByYearAndTypeAndCommodity(String.valueOf(i), type,  commodity);
                        listByYear.add(commodityByYear);
                    }
                    ThailandExcel excel = new ThailandExcel(listByYear, type, commodity);
                    excel.saveAll();
                }
//                listByYear = new ArrayList<>();
//                List<String> subComms = getSubCommoditiesByParent(commodity);
//                System.out.println(subComms);
//                for (String subComm: subComms) {
//                    for (String type: petroleumProductsTypes) {
//                        for (int i = 2017; i < Integer.parseInt(getLatestYear().get(0)) + 1; i++) {
//                            List<Thailand> commodityByYear = retrieveAllThailandByYearAndTypeAndCommodity(String.valueOf(i), type,  subComm);
//                            listByYear.add(commodityByYear);
//                        }
//                        ThailandExcel excel = new ThailandExcel(listByYear, type, commodity);
//                        excel.saveAll();
//                    }
//                }

            }
        }
    }



    public void deleteAll() {
        repo.deleteAll();
    }
}

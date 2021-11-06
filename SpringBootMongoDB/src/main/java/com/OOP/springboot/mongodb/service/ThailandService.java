package com.OOP.springboot.mongodb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.Thailand;
import com.OOP.springboot.mongodb.repository.ThailandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThailandService {
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

    public List<Thailand> retrieveAllThailandByYearAndMonthAndTypeAndCommodityAndRegion(String year, String month, String type, String commodity, String region) {
        return repo.findByYearAndMonthAndTypeAndCommodityAndRegion(year, month, type, commodity, region);
    }

    public List<Thailand> retrieveAllThailandByYearAndMonthAndTypeAndCommodityAndContinent(String year, String month, String type, String commodity, String continent) {
        return repo.findByYearAndMonthAndTypeAndCommodityAndContinent(year, month, type, commodity, continent);
    }

    public List<String> getAllDistinctCommodities() { return repo.findDistinctCommodities();}

    public List<String> getAllDistinctRegions() { return repo.findDistinctRegions();}

    public List<String> getAllDistinctContinents() { return repo.findDistinctContinents();}

    public List<String> getAllDistinctRefineries() { return repo.findDistinctRefineries();}

    public List<String> getAllDistinctYears() { return repo.findDistinctYears();}

    public void deleteAll() {
        repo.deleteAll();
    }
}

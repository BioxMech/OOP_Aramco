package com.OOP.springboot.mongodb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.ThailandCrudeOil;
import com.OOP.springboot.mongodb.repository.ThailandCrudeOilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThailandCrudeOilService {
    @Autowired
    ThailandCrudeOilRepository repo;

    public ThailandCrudeOil saveThailandCrudeOil(ThailandCrudeOil thailandCrudeOil) {
        return repo.save(thailandCrudeOil);
    }

    public List<ThailandCrudeOil> saveListThailandCrudeOil(List<Map<String, String>> thailandCrudeOil) {
        List<ThailandCrudeOil> writeData = new ArrayList<>();
        for (Map<String, String> data: thailandCrudeOil) {
            List<ThailandCrudeOil> searchResult = this.retrieveAllCrudeOilByYearAndMonthAndRegionAndType(data.get("year"), data.get("month"), data.get("region"), data.get("type"));
            if (searchResult.size() == 0) {
                writeData.add(new ThailandCrudeOil(data));
            }
        }
        return repo.saveAll(writeData);
    }

    public List<ThailandCrudeOil> retrieveAllThailandCrudeOil() {
        return repo.findAll();
    }

    public List<ThailandCrudeOil> retrieveAllCrudeOilByYear(String year) {
        return repo.findByYear(year);
    }

    public List<ThailandCrudeOil> retrieveAllCrudeOilByYearAndMonth(String year, String month) {
        return repo.findByYearAndMonth(year, month);
    }

    public List<ThailandCrudeOil> retrieveAllCrudeOilByYearAndMonthAndRegion(String year, String month, String region) {
        return repo.findByYearAndMonthAndRegion(year, month, region);
    }

    public List<ThailandCrudeOil> retrieveAllCrudeOilByYearAndMonthAndRegionAndType(String year, String month, String region, String type) {
        return repo.findByYearAndMonthAndRegionAndType(year, month, region, type);
    }

    public void deleteAll() {
        repo.deleteAll();
    }
}

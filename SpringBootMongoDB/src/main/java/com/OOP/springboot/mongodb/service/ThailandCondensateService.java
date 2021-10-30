package com.OOP.springboot.mongodb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.ThailandCondensate;
import com.OOP.springboot.mongodb.repository.ThailandCondensateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThailandCondensateService {
    @Autowired
    ThailandCondensateRepository repo;

    public ThailandCondensate saveThailandCondensate(ThailandCondensate thailandCondensate) {
        return repo.save(thailandCondensate);
    }

    public List<ThailandCondensate> saveListThailandCondensate(List<Map<String, String>> thailandCondensate) {
        List<ThailandCondensate> writeData = new ArrayList<>();
        for (Map<String, String> data: thailandCondensate) {
            List<ThailandCondensate> searchResult = this.retrieveAllCondensateByYearAndMonthAndRegion(data.get("year"), data.get("month"), data.get("region"));
            if (searchResult.size() == 0) {
                writeData.add(new ThailandCondensate(data));
            }
        }
        return repo.saveAll(writeData);
    }

    public List<ThailandCondensate> retrieveAllThailandCondensate() {
        return repo.findAll();
    }

    public List<ThailandCondensate> retrieveAllCondensateByYear(String year) {
        return repo.findByYear(year);
    }

    public List<ThailandCondensate> retrieveAllCondensateByYearAndMonth(String year, String month) {
        return repo.findByYearAndMonth(year, month);
    }

    public List<ThailandCondensate> retrieveAllCondensateByYearAndMonthAndRegion(String year, String month, String region) {
        return repo.findByYearAndMonthAndRegion(year, month, region);
    }

    public void deleteAll() {
        repo.deleteAll();
    }
}

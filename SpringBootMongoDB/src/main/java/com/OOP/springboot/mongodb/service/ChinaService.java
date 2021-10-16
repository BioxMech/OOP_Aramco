package com.OOP.springboot.mongodb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.China;
import com.OOP.springboot.mongodb.repository.ChinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ChinaService {

    @Autowired
    ChinaRepository repo;

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
        return repo.findByYearAndCommodity(year, commodity);
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
    public void deleteAll() { repo.deleteAll(); }
}

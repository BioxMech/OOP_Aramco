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
            writeData.add(new ThailandCrudeOil(data));
        }
        return repo.saveAll(writeData);
    }

    public List<ThailandCrudeOil> retrieveAllThailandCrudeOil() {
        return repo.findAll();
    }

    public void deleteAll() {
        repo.deleteAll();
    }
}

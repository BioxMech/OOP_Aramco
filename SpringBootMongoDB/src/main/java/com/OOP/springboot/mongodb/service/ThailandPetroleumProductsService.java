package com.OOP.springboot.mongodb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.ThailandPetroleumProducts;
import com.OOP.springboot.mongodb.repository.ThailandPetroleumProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThailandPetroleumProductsService {
    @Autowired
    ThailandPetroleumProductsRepository repo;

    public ThailandPetroleumProducts saveThailandPetroleumProducts(ThailandPetroleumProducts thailandPetroleumProducts) {
        return repo.save(thailandPetroleumProducts);
    }

    public List<ThailandPetroleumProducts> saveListThailandPetroleumProducts(List<Map<String, String>> thailandPetroleumProducts) {
        List<ThailandPetroleumProducts> writeData = new ArrayList<>();
        for (Map<String, String> data: thailandPetroleumProducts) {
            writeData.add(new ThailandPetroleumProducts(data));
        }
        return repo.saveAll(writeData);
    }

    public List<ThailandPetroleumProducts> retrieveAllThailandCondensate() {
        return repo.findAll();
    }

    public void deleteAll() {
        repo.deleteAll();
    }
}

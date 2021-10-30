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
            List<ThailandPetroleumProducts> searchResult = this.retrieveAllPetroleumProductsByYearAndMonthAndCommodityAndType(data.get("year"), data.get("month"), data.get("commodity"), data.get("type"));
            if (searchResult.size() == 0) {
                writeData.add(new ThailandPetroleumProducts(data));
            }
        }
        return repo.saveAll(writeData);
    }

    public List<ThailandPetroleumProducts> retrieveAllThailandPetroleumProducts() { return repo.findAll();}

    public List<ThailandPetroleumProducts> retrieveAllPetroleumProductsByYear(String year) {
        return repo.findByYear(year);
    }

    public List<ThailandPetroleumProducts> retrieveAllPetroleumProductsByYearAndMonth(String year, String month) {
        return repo.findByYearAndMonth(year, month);
    }

    public List<ThailandPetroleumProducts> retrieveAllPetroleumProductsByYearAndCommodity(String year, String commodity) {
        return repo.findByYearAndCommodity(year, commodity);
    }

    public List<ThailandPetroleumProducts> retrieveAllPetroleumProductsByYearAndMonthAndCommodity(String year, String month, String commodity) {
        return repo.findByYearAndMonthAndCommodity(year, month, commodity);
    }

    public List<ThailandPetroleumProducts> retrieveAllPetroleumProductsByYearAndMonthAndCommodityAndType(String year, String month, String commodity, String type) {
        return repo.findByYearAndMonthAndCommodityAndType(year, month, commodity, type);
    }

    public void deleteAll() {
        repo.deleteAll();
    }
}

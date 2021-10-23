package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.ThailandPetroleumProducts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThailandPetroleumProductsRepository extends MongoRepository<ThailandPetroleumProducts, String> {
}

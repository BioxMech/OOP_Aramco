package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.ThailandCondensate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThailandCondensateRepository extends MongoRepository<ThailandCondensate, String> {
}

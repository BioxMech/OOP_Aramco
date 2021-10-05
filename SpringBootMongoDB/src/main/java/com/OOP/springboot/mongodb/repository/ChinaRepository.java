package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.China;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChinaRepository extends MongoRepository<China, String>{
}

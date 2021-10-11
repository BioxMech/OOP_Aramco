package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.China;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChinaRepository extends MongoRepository<China, String>{

}

package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String>{
}
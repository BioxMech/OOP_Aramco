package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.s3;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface s3Repository extends MongoRepository<s3, String>{

    // Find all links by country
    List<s3> findByCountry(String country);

    // Find link of specific country and commodity
    List <s3> findByCountryAndCommodityLikeOrderByDateDesc(String country, String commodity);

    // Find link of specific country and commodity
    List <s3> findByCountryAndCommodityLikeOrderByDateAsc(String country, String commodity);

}
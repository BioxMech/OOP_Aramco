package com.OOP.springboot.mongodb.service;

import com.OOP.springboot.mongodb.model.s3;
import com.OOP.springboot.mongodb.repository.s3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class s3Service {

    @Autowired
    s3Repository repo;

    public List<s3> retrieveS3LinksByCountry(String country){
        return repo.findByCountry(country);
    }

    public List<s3> retrieveS3LinksByCountryAndCommodity(String country, String commodity){

        return repo.findByCountryAndCommodityLikeOrderByDateDesc(country, commodity);
    }

    public s3 saves3(s3 s3) { return repo.save(s3); }



}

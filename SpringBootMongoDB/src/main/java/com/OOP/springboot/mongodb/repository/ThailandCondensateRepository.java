package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.ThailandCondensate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThailandCondensateRepository extends MongoRepository<ThailandCondensate, String> {

    // Find all data in specified year
    List<ThailandCondensate> findByYear(String year);

    // Find all data in specified year according to commodity
    List<ThailandCondensate> findByYearAndCommodity(String year, String commodity);

    // Find data in specified year and specified month
    List<ThailandCondensate> findByYearAndMonth(String year, String month);

    // Find data in specified year, specified month and specified region
    List<ThailandCondensate> findByYearAndMonthAndRegion(String year, String month, String region);

    // Find all data in the specified year according to the commodity and type
    List <ThailandCondensate> findByYearAndTypeAndCommodity(String year, String type, String commodity);


    List<ThailandCondensate> findFirstByOrderByYearDescMonthDesc();
}

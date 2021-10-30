package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.ThailandCrudeOil;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThailandCrudeOilRepository extends MongoRepository<ThailandCrudeOil, String> {

    // Find all data in specified year
    List<ThailandCrudeOil> findByYear(String year);

    // Find all data in specified year according to commodity
    List<ThailandCrudeOil> findByYearAndCommodity(String year, String commodity);

    // Find data in specified year and specified month
    List<ThailandCrudeOil> findByYearAndMonth(String year, String month);

    // Find data in specified year, specified month and specified region
    List<ThailandCrudeOil> findByYearAndMonthAndRegion(String year, String month, String region);

    // Find data in specified year, specified month and specified region and type
    List<ThailandCrudeOil> findByYearAndMonthAndRegionAndType(String year, String month, String region, String type);

    // Find all data in the specified year according to the commodity and type
    List <ThailandCrudeOil> findByYearAndTypeAndCommodity(String year, String type, String commodity);

    List<ThailandCrudeOil> findFirstByOrderByYearDescMonthDesc();
}

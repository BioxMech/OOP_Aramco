package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.Thailand;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThailandRepository extends MongoRepository<Thailand, String> {
    // Find all data in specified year
    List<Thailand> findByYear(String year);

    // Find all data in specified year according to commodity
    List<Thailand> findByYearAndCommodity(String year, String commodity);

    // Find all data in the specified year according to the commodity and type
    List <Thailand> findByYearAndTypeAndCommodity(String year, String type, String commodity);

    // Find all data in the specified year and month according to the commodity and type
    List <Thailand> findByYearAndMonthAndTypeAndCommodity(String year, String month, String type, String commodity);

    // Find data in specified year, specified month and specified region
    List<Thailand> findByYearAndMonthAndRegion(String year, String month, String region);

    // Find data in specified year, specified month and specified refinery
    List<Thailand> findByYearAndMonthAndRefinery(String year, String month, String refinery);

    // Find data in specified year, specified month and specified region and type
    List<Thailand> findByYearAndMonthAndTypeAndCommodityAndRegion(String year, String month, String type, String commodity,String region);

    // Find data in specified year, specified month and specified region and type
    List<Thailand> findByYearAndMonthAndTypeAndCommodityAndContinent(String year, String month, String type, String commodity,String continent);

    @Aggregation(pipeline = { "{ '$group': { '_id' : '$commodity' } }" })
    List<String> findDistinctCommodities();

    @Aggregation(pipeline = { "{ '$group': { '_id' : '$region' } }" })
    List<String> findDistinctRegions();

    @Aggregation(pipeline = { "{ '$group': { '_id' : '$continent' } }" })
    List<String> findDistinctContinents();

    @Aggregation(pipeline = { "{ '$group': { '_id' : '$refinery' } }" })
    List<String> findDistinctRefineries();

    @Aggregation(pipeline = { "{ '$group': { '_id' : '$year' } }" })
    List<String> findDistinctYears();

    List<Thailand> findFirstByOrderByYearDescMonthDesc();
}

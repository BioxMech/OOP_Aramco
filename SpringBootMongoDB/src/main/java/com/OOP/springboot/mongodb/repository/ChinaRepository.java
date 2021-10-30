package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.China;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChinaRepository extends MongoRepository<China, String>{

    // Find all data in the specified year
    List <China> findByYear(String year);

    // Find all data in the specified year according to the commodity
    List <China> findByYearAndCommodity(String year, String commodity);

    // Find all data in the specified year according to the commodity and type
    List <China> findByYearAndTypeAndCommodity(String year, String type, String commodity);

    List<China> findFirstByOrderByYearDescMonthDesc();

    List<China> findByYearAndMonth(String year, String month);

    @Aggregation(pipeline = { "{ '$group': { '_id' : '$commodity' } }" })
    List<String> findDistinctCommodities();

    @Aggregation(pipeline = { "{ '$group': { '_id' : '$year' } }" })
    List<String> findDistinctYears();
}

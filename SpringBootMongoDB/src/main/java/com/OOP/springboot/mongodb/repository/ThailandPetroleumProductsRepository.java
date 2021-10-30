package com.OOP.springboot.mongodb.repository;

import com.OOP.springboot.mongodb.model.ThailandPetroleumProducts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThailandPetroleumProductsRepository extends MongoRepository<ThailandPetroleumProducts, String> {

    // Find all data in specified year
    List<ThailandPetroleumProducts> findByYear(String year);

    // Find all data in specified year according to commodity
    List<ThailandPetroleumProducts> findByYearAndCommodity(String year, String commodity);

    // Find all data in specified year, month according to commodity
    List<ThailandPetroleumProducts> findByYearAndMonthAndCommodity(String year, String month, String commodity);

    // Find all data in the specified year according to the commodity and type
    List <ThailandPetroleumProducts> findByYearAndTypeAndCommodity(String year, String type, String commodity);


    // Find data in specified year and specified month
    List<ThailandPetroleumProducts> findByYearAndMonth(String year, String month);

    // Find data in specified year, specified month and specified region
//    List<ThailandPetroleumProducts> findByYearAndMonthAndRegion(String year, String month, String region);

    // Find data in specified year, specified month and specified region and type
//    List<ThailandPetroleumProducts> findByYearAndMonthAndRegionAndType(String year, String month, String region, String type);

    // Find data in specified year, specified month, commodity and type
    List<ThailandPetroleumProducts> findByYearAndMonthAndCommodityAndType(String year, String month, String commodity, String type);

    List<ThailandPetroleumProducts> findFirstByOrderByYearDescMonthDesc();
}

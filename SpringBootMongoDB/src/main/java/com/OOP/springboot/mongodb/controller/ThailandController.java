package com.OOP.springboot.mongodb.controller;

import com.OOP.springboot.mongodb.model.ThailandCrudeOil;
import com.OOP.springboot.mongodb.model.ThailandCondensate;
import com.OOP.springboot.mongodb.model.ThailandPetroleumProducts;
import com.OOP.springboot.mongodb.service.ThailandCrudeOilService;
import com.OOP.springboot.mongodb.service.ThailandCondensateService;
import com.OOP.springboot.mongodb.service.ThailandPetroleumProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/thailand")
public class ThailandController {
    @Autowired
    ThailandCrudeOilService thailandCrudeOilService;
    @Autowired
    ThailandCondensateService thailandCondensateService;
    @Autowired
    ThailandPetroleumProductsService thailandPetroleumProductsService;

    /* Crude Oil Retrieval*/
    @GetMapping("/crudeOil")
    public ResponseEntity<List<ThailandCrudeOil>> getAllCrudeOil(HttpServletRequest request) {
        try {
            List<ThailandCrudeOil> crudeOil = thailandCrudeOilService.retrieveAllThailandCrudeOil();
            return new ResponseEntity<>(crudeOil, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/crudeOil/{year}")
    public ResponseEntity<List<ThailandCrudeOil>> getAllCrudeOilByYear(@PathVariable String year,HttpServletRequest request) {
        try {
            List<ThailandCrudeOil> crudeOil = thailandCrudeOilService.retrieveAllCrudeOilByYear(year);
            return new ResponseEntity<>(crudeOil, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/crudeOil/{year}/{month}")
    public ResponseEntity<List<ThailandCrudeOil>> getAllCrudeOilByYearAndMonth(@PathVariable String year, @PathVariable String month, HttpServletRequest request) {
        try {
            List<ThailandCrudeOil> crudeOil = thailandCrudeOilService.retrieveAllCrudeOilByYearAndMonth(year, month);
            return new ResponseEntity<>(crudeOil, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/crudeOil/{year}/{month}/{region}")
    public ResponseEntity<List<ThailandCrudeOil>> getAllCrudeOilByYearAndMonthAndRegion(@PathVariable String year, @PathVariable String month, @PathVariable String region, HttpServletRequest request) {
        try {
            List<ThailandCrudeOil> crudeOil = thailandCrudeOilService.retrieveAllCrudeOilByYearAndMonthAndRegion(year, month, region);
            return new ResponseEntity<>(crudeOil, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/crudeOil/{year}/{month}/{region}/{type}")
    public ResponseEntity<List<ThailandCrudeOil>> getAllCrudeOilByYearAndMonthAndRegionAndType(@PathVariable String year, @PathVariable String month, @PathVariable String region, @PathVariable String type, HttpServletRequest request) {
        try {
            List<ThailandCrudeOil> crudeOil = thailandCrudeOilService.retrieveAllCrudeOilByYearAndMonthAndRegionAndType(year, month, region, type);
            return new ResponseEntity<>(crudeOil, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* Condensate Retrieval*/
    @GetMapping("/Condensate")
    public ResponseEntity<List<ThailandCondensate>> getAllCondensate(HttpServletRequest request) {
        try {
            List<ThailandCondensate> Condensate = thailandCondensateService.retrieveAllThailandCondensate();
            return new ResponseEntity<>(Condensate, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/Condensate/{year}")
    public ResponseEntity<List<ThailandCondensate>> getAllCondensateByYear(@PathVariable String year,HttpServletRequest request) {
        try {
            List<ThailandCondensate> Condensate = thailandCondensateService.retrieveAllCondensateByYear(year);
            return new ResponseEntity<>(Condensate, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/Condensate/{year}/{month}")
    public ResponseEntity<List<ThailandCondensate>> getAllCondensateByYearAndMonth(@PathVariable String year, @PathVariable String month, HttpServletRequest request) {
        try {
            List<ThailandCondensate> Condensate = thailandCondensateService.retrieveAllCondensateByYearAndMonth(year, month);
            return new ResponseEntity<>(Condensate, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/Condensate/{year}/{month}/{region}")
    public ResponseEntity<List<ThailandCondensate>> getAllCondensateByYearAndMonthAndRegion(@PathVariable String year, @PathVariable String month, @PathVariable String region, HttpServletRequest request) {
        try {
            List<ThailandCondensate> Condensate = thailandCondensateService.retrieveAllCondensateByYearAndMonthAndRegion(year, month, region);
            return new ResponseEntity<>(Condensate, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /* Petroleum Products Retrieval*/
    @GetMapping("/petroleumProducts")
    public ResponseEntity<List<ThailandPetroleumProducts>> getAllPetroleum(HttpServletRequest request) {
        try {
            List<ThailandPetroleumProducts> petroleum = thailandPetroleumProductsService.retrieveAllThailandPetroleumProducts();
            return new ResponseEntity<>(petroleum, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/petroleumProducts/{year}")
    public ResponseEntity<List<ThailandPetroleumProducts>> getAllPetroleumProductsByYear(@PathVariable String year,HttpServletRequest request) {
        try {
            List<ThailandPetroleumProducts> petroleum = thailandPetroleumProductsService.retrieveAllPetroleumProductsByYear(year);
            return new ResponseEntity<>(petroleum, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/petroleumProducts/{year}/{month}")
    public ResponseEntity<List<ThailandPetroleumProducts>> getAllPetroleumProductsByYearAndMonth(@PathVariable String year, @PathVariable String month, HttpServletRequest request) {
        try {
            List<ThailandPetroleumProducts> petroleum = thailandPetroleumProductsService.retrieveAllPetroleumProductsByYearAndMonth(year, month);
            return new ResponseEntity<>(petroleum, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/petroleumProducts/{year}/{commodity}")
    public ResponseEntity<List<ThailandPetroleumProducts>> getAllPetroleumProductsByYearAndCommodity(@PathVariable String year, @PathVariable String commodity, HttpServletRequest request) {
        try {
            String convertedCommodity = commodity.replaceAll("[%20]", " ");
            List<ThailandPetroleumProducts> petroleum = thailandPetroleumProductsService.retrieveAllPetroleumProductsByYearAndCommodity(year, convertedCommodity);
            return new ResponseEntity<>(petroleum, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/petroleumProducts/{year}/{month}/{commodity}")
    public ResponseEntity<List<ThailandPetroleumProducts>> getAllPetroleumProductsByYearAndMonthAndCommodity(@PathVariable String year, @PathVariable String month,@PathVariable String commodity, HttpServletRequest request) {
        try {
            String convertedCommodity = commodity.replaceAll("[%20]", " ");
            List<ThailandPetroleumProducts> petroleum = thailandPetroleumProductsService.retrieveAllPetroleumProductsByYearAndMonthAndCommodity(year, month, convertedCommodity);
            return new ResponseEntity<>(petroleum, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/petroleumProducts/{year}/{month}/{commodity}/{type}")
    public ResponseEntity<List<ThailandPetroleumProducts>> getAllPetroleumProductsByYearAndMonthAndCommodityAndType(@PathVariable String year, @PathVariable String month, @PathVariable String commodity, @PathVariable String type, HttpServletRequest request) {
        try {
            String convertedCommodity = commodity.replaceAll("[%20]", " ");
            System.out.println(convertedCommodity);
            List<ThailandPetroleumProducts> petroleum = thailandPetroleumProductsService.retrieveAllPetroleumProductsByYearAndMonthAndCommodityAndType(year, month, convertedCommodity, type);
            return new ResponseEntity<>(petroleum, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

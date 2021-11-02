package com.OOP.springboot.mongodb.controller;

import com.OOP.springboot.mongodb.model.Thailand;
import com.OOP.springboot.mongodb.service.ThailandService;
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
    ThailandService thailandService;

    /* Thailand Retrieval */
    @GetMapping("/all")
    public ResponseEntity<List<Thailand>> getAllThailand(HttpServletRequest request) {
        try {
            List<Thailand> thailand = thailandService.retrieveAllThailand();
            return new ResponseEntity<>(thailand, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{year}")
    public ResponseEntity<List<Thailand>> getAllThailandByYear(@PathVariable String year, HttpServletRequest request) {
        try {
            List<Thailand> thailand = thailandService.retrieveAllThailandByYear(year);
            return new ResponseEntity<>(thailand, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{year}/{commodity}")
    public ResponseEntity<List<Thailand>> getAllThailandByYearAndCommodity(@PathVariable String year, @PathVariable String commodity, HttpServletRequest request) {
        try {
            List<Thailand> thailand = thailandService.retrieveAllThailandByYearAndCommodity(year, commodity);
            return new ResponseEntity<>(thailand, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{year}/{type}/{commodity}")
    public ResponseEntity<List<Thailand>> getAllThailandByYearAndTypeAndCommodity(@PathVariable String year, @PathVariable String type, @PathVariable String commodity, HttpServletRequest request) {
        try {
            String convertedCommodity = commodity.replaceAll("[%20]", " ");
            List<Thailand> thailand = thailandService.retrieveAllThailandByYearAndTypeAndCommodity(year, type, convertedCommodity);
            return new ResponseEntity<>(thailand, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{year}/{month}/{type}/{commodity}")
    public ResponseEntity<List<Thailand>> getAllThailandByYearAndMonthAndTypeAndCommodity(@PathVariable String year, @PathVariable String month, @PathVariable String type, @PathVariable String commodity, HttpServletRequest request) {
        try {
            String convertedCommodity = commodity.replaceAll("[%20]", " ");
            List<Thailand> thailand = thailandService.retrieveAllThailandByYearAndMonthAndTypeAndCommodity(year, month, type, convertedCommodity);
            return new ResponseEntity<>(thailand, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{year}/{month}/{type}/{commodity}/{region}")
    public ResponseEntity<List<Thailand>> getAllThailandByYearAndMonthAndTypeAndCommodityAndRegion(@PathVariable String year, @PathVariable String month, @PathVariable String type, @PathVariable String commodity, @PathVariable String region, HttpServletRequest request) {
        List<Thailand> thailand = null;
        try {
            String convertedCommodity = commodity.replaceAll("[%20]", " ");
            String convertedRegion = region.replaceAll("[%20]", " ");
            System.out.println(convertedRegion);
            System.out.println(type.equals("import"));
            if (type.equals("import")) {
                if ( convertedCommodity.equals("Crude Oil")) {
                    String continent = convertedRegion;
                    thailand = thailandService.retrieveAllThailandByYearAndMonthAndTypeAndCommodityAndContinent(year, month, type, convertedCommodity, continent);
                }

            } else {
                thailand = thailandService.retrieveAllThailandByYearAndMonthAndTypeAndCommodityAndRegion(year, month, type, convertedCommodity, convertedRegion);
            }

            return new ResponseEntity<>(thailand, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/commodities")
    public ResponseEntity<List<String>> getDistinctCommodities() {
        try {
            List<String> distinctCommodities = thailandService.getAllDistinctCommodities();
            return new ResponseEntity<>(distinctCommodities, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/regions")
    public ResponseEntity<List<String>> getDistinctRegions() {
        try {
            List<String> distinctRegions = thailandService.getAllDistinctRegions();
            return new ResponseEntity<>(distinctRegions, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/continents")
    public ResponseEntity<List<String>> getDistinctContinents() {
        try {
            List<String> distinctContinents = thailandService.getAllDistinctContinents();
            return new ResponseEntity<>(distinctContinents, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
package com.OOP.springboot.mongodb.controller;

import com.OOP.springboot.mongodb.model.China;
import com.OOP.springboot.mongodb.model.s3;
import com.OOP.springboot.mongodb.repository.ChinaRepository;
import com.OOP.springboot.mongodb.service.ChinaService;
import com.OOP.springboot.mongodb.service.s3Service;
import com.OOP.springboot.mongodb.service.utils.ChinaExcel;
import com.OOP.springboot.mongodb.service.utils.ChinaLinkScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/china")
public class ChinaController {

    @Autowired
    ChinaService chinaService;

    public String saveChina(China china) {
        try {
            China _china = chinaService.saveChina(china);
            return "Successfully uploaded one China data to MongoDB with id = " + _china.getId();
        }catch(Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<China>> getAllChina(HttpServletRequest request) {
        try {

            List<China> china = chinaService.retrieveAllChina();
            return new ResponseEntity<>(china, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{year}")
    public ResponseEntity<List<China>> getAllChinaByYear(@PathVariable String year, HttpServletRequest request) {
        try {

            List<China> china = chinaService.retrieveAllChinaByYear(year);
            return new ResponseEntity<>(china, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{year}/{commodity}")
    public ResponseEntity<List<China>> getAllChinaByYear(@PathVariable String year, @PathVariable String commodity, HttpServletRequest request) {
        try {

            List<China> china = chinaService.retrieveAllChinaByYearAndCommodity(year, commodity);
            return new ResponseEntity<>(china, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{year}/{type}/{commodity}")
    public ResponseEntity<List<China>> getAllChinaByYear(@PathVariable String year, @PathVariable String commodity, @PathVariable String type, HttpServletRequest request) {
        try {

            List<China> china = chinaService.retrieveAllChinaByYearAndTypeAndCommodity(year, type, commodity);
            return new ResponseEntity<>(china, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/commodities")
    public ResponseEntity<List<String>> getDistinctCommodities(){
        try {
            List<String> distinctCommodities = chinaService.getAllDistinctCommodities();
            return new ResponseEntity<>(distinctCommodities, HttpStatus.OK);
        } catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/years")
    public ResponseEntity<List<String>> getDistinctYears(){
        try {
            List<String> distinctCommodities = chinaService.getAllDistinctYears();
            return new ResponseEntity<>(distinctCommodities, HttpStatus.OK);
        } catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/saveallexcel")
    public String saveAllExcelFiles() {

        try {
            chinaService.saveAllExcelFiles();
            return "Successfully uploaded all excel files";
        } catch (Exception e){
            return e.getMessage();
        }

    }
}

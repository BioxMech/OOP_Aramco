package com.OOP.springboot.mongodb.controller;

import com.OOP.springboot.mongodb.message.ResponseMsg;
import com.OOP.springboot.mongodb.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scrape")
public class CrawlerController {

    @Autowired
    CrawlerService crawlerService;

    @GetMapping("/thailand")
    public ResponseEntity<ResponseMsg> getThailandScrapeData(HttpServletRequest request) {
        try {

            List<Map<String, String>> dataObjects = crawlerService.scrapeThailand();

            String message = "Thailand - Crawling successful!";

            return new ResponseEntity<ResponseMsg>(new ResponseMsg(message,
                    request.getRequestURI(), dataObjects, true), HttpStatus.OK);
        } catch(Exception e) {
            String message = "Crawling failed";
            return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(),
                    e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: China web scraping controller
    @GetMapping("/china")
    public ResponseEntity<ResponseMsg> getChinaScrapeData(HttpServletRequest request) {
        try {

            List<Map<String, String>> dataObjects;
            dataObjects = crawlerService.scrapeChina();
            String message = "China - Crawling successfully!";

            return new ResponseEntity<ResponseMsg>(new ResponseMsg(message,
                    request.getRequestURI(), dataObjects, true, "China"), HttpStatus.OK);
        } catch(Exception e) {
            String message = "Crawling failed";
            return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(),
                    e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

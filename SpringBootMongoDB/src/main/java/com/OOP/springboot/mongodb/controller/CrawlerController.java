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

@RestController
@RequestMapping("/api/scrape")
public class CrawlerController {

    @Autowired
    CrawlerService crawlerService;

    @GetMapping("/thailand")
    public ResponseEntity<ResponseMsg> getThailandScrapeData(HttpServletRequest request) {
        try {
            // get all documents from MongoDB database
            List<String> links = crawlerService.scrapeThailand("http://www.eppo.go.th/index.php/en/en-energystatistics/petroleum-statistic");

            String message = "Crawling successfully!";

            return new ResponseEntity<ResponseMsg>(new ResponseMsg(message,
                    request.getRequestURI(), links, true), HttpStatus.OK);
        } catch(Exception e) {
            String message = "Crawling failed";
            return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(),
                    e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: China web scraping controller
}

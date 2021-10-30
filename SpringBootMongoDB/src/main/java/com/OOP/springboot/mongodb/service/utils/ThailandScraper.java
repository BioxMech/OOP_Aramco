//package com.OOP.springboot.mongodb.service.utils;
//
//import com.OOP.springboot.mongodb.service.ThailandCrudeOilService;
//import org.apache.commons.lang3.StringUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public abstract class ThailandScraper {
//    private String URL;
//    private String rowName;
//    private final String[] monthHeaders;
//
//    public ThailandScraper(String URL, String rowName, String[] monthHeaders) {
//        this.URL = URL;
//        this.rowName = rowName;
//        this.monthHeaders = monthHeaders;
//    }
//
//    public String getURL() {
//        return this.URL;
//    }
//
//    public String getRowName() {
//        return this.rowName;
//    }
//
//    public static ThailandScraper getInstance(String URL, String rowName ) {
//        if (URL.contains("T02_01_01")) {
//            return new ThailandCrudeOilProductionScraper(URL, rowName);
//        }
//        if (URL.contains("T02_03_02")) {
//            return new ThailandPetroleumProductsProductionScraper(URL, rowName);
//        } else {
//            return new ThailandPetroleumProductsSalesScraper(URL, rowName);
//        }
//    }
//
//    public abstract List<Map<String, String>> scrapeThailand();
//
//}

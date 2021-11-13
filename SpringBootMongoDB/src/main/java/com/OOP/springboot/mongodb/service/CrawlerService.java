package com.OOP.springboot.mongodb.service;

import com.OOP.springboot.mongodb.service.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class CrawlerService {
    @Autowired
    ChinaService chinaService;
    @Autowired
    ApplicationContext appContext;
    @Autowired
    ThailandService thailandService;

    private final List<String> links;
    private final HashMap<String, String> thailandLinks = new HashMap<>();
    private final List<String> thailandDataRequiredTitle = new ArrayList<>(Arrays.asList(
            "Table 2.1-1: Production of Crude Oil",
            "Table 2.1-2: Production of Condensate",
            "Table 2.1-3: Import of Crude Oil Classified by Sources",
            "Table 2.1-5: Quantity and Value of Petroleum Products Export",
            "Table 2.2-2: Material Intake",
            "Table 2.3-2: Production of Petroleum Products (Barrel/Day)",
            "Table 2.3-4: Sale of Petroleum Products (Barrel/Day)",
            "Table 2.3-7: Import of Petroleum Products (Barrel/Day)",
            "Table 2.3-9: Export of Petroleum Products (Barrel/Day)",
            "Table 2.3-11: Net Export of Petroleum Products (Barrel/Day)"
    ));
    private ChinaLinkScraper chinaLinkScraper;
    public CrawlerService(List<String> links) {
        this.links = new ArrayList<>();
    }

    // Thailand Web Scraping Service
     @Scheduled(cron = "0 30 16 * * *") // 3 Am everyday
    public List<Map<String, String>> scrapeThailand() {
        String URL = "http://www.eppo.go.th/index.php/en/en-energystatistics/petroleum-statistic";
        // Initialize list
        List<Map<String, String>> dataObjects = new ArrayList<>();


        try {
            // Fetch the HTML code
            Document document = Jsoup.connect(URL).get();
            // Parse the HTML to extract links to other URLs
            Elements elementData = document.select(".catItemBody");

            // Store a local variable of the link when looping (for better debugging purpose too)
            String link;

            for (Element e : elementData) {
                // Go to the 1st div element and look at its text (in terms of js, value)
                String rowName = e.firstElementSibling().selectFirst("div").text();
                // Check if it is the right row we are looking for
                if (thailandDataRequiredTitle.contains(rowName)) {
                    // Obtain the link
                    link = e.getElementsByIndexEquals(2).select("a").attr("abs:href");
                    thailandLinks.put(rowName, link);
                    links.add(link);
                }
            }
            System.out.println(thailandLinks);
            for (Map.Entry<String, String> entry : thailandLinks.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value.contains("T02_01_01")) {
                    try {
//                        System.out.println("Crude Oil Production Scraper Called");
                        ThailandCrudeOilProductionScraper crudeOilProductionExcelScraper = new ThailandCrudeOilProductionScraper(value, key);
                        dataObjects.addAll(crudeOilProductionExcelScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }

                }
                if (value.contains("T02_01_02")) {
                    try {
                        ThailandCondensateProductionScraper condensateProductionExcelScraper = new ThailandCondensateProductionScraper(value, key);
                        dataObjects.addAll(condensateProductionExcelScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                if (value.contains("T02_01_03")) {
                    try {
                        ThailandCrudeOilImportScraper crudeOilImportScraper = new ThailandCrudeOilImportScraper(value, key);
                        dataObjects.addAll(crudeOilImportScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                if (value.contains("T02_02_02")) {
                    try {
                        ThailandMaterialIntakeScraper materialIntakeScraper = new ThailandMaterialIntakeScraper(value, key);
                        dataObjects.addAll(materialIntakeScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                if (value.contains("T02_03_02")) {
                    try {
                        ThailandPetroleumProductsProductionScraper petroleumProductsProductionScraper = new ThailandPetroleumProductsProductionScraper(value, key);
                        dataObjects.addAll(petroleumProductsProductionScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                if (value.contains("T02_03_04")) {
                    try {
                        ThailandPetroleumProductsSalesScraper petroleumProductsSalesScraper = new ThailandPetroleumProductsSalesScraper(value, key);
                        dataObjects.addAll(petroleumProductsSalesScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }

//                EK ADDED HERE
//                Petroleum Products (Export & Import)
                if ((value.contains("T02_03_09")) || (value.contains("T02_03_07"))) {
                    try {
                        ThailandPetroleumProductsImportExportScraper petroleumProductsImportExportScraper = new ThailandPetroleumProductsImportExportScraper(value, key);
                        dataObjects.addAll(petroleumProductsImportExportScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }

//                Quantity and Value of Crude Oil / Petroleum Products
                if (value.contains("T02_01_05")){
                    try {
                        ThailandCrudeOilPetroleumProductsQtyValImportExportScraper crudeOilPetroleumProductsQtyValImportExportScraper = new ThailandCrudeOilPetroleumProductsQtyValImportExportScraper(value, key);
                        dataObjects.addAll(crudeOilPetroleumProductsQtyValImportExportScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }

//                Net Exports
                if (value.contains("T02_03_11")) {
                    try {
                        ThailandPetroleumProductsNetExportScraper petroleumProductsNetExportScraper = new ThailandPetroleumProductsNetExportScraper(value, key);
                        dataObjects.addAll(petroleumProductsNetExportScraper.scrapeThailand());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }

            }
            thailandService.saveListThailand(dataObjects);
        } catch (IOException e) { // Same as the above - if URL cannot be found
            System.err.println("For '" + URL + "': " + e.getMessage());
        }

        return dataObjects;
    }


    // TODO: China Web scraping service

        // TODO Retrieve and store XLS file
    //  China Web scraping service
//    @Scheduled(cron = "0 00 03 * * ?") // 3 Am everyday
    public List<Map<String, String>> scrapeChina() throws IOException {
        // Initialize list
        List<Map<String,String>> dataObjects = new ArrayList<>();

        try {
            chinaLinkScraper = appContext.getBean(ChinaLinkScraper.class);
            links.addAll(chinaLinkScraper.scrapeMissing());
            /* Retrieve data from the extracted links */
            for (String url: links){
                ChinaPageScraper pageScraper = ChinaPageScraper.getInstance(url);
                dataObjects.addAll(pageScraper.extractData());
            }
            chinaService.saveListChina(dataObjects);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return dataObjects;
    }
}


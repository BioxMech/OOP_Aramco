package com.OOP.springboot.mongodb.service;

import com.OOP.springboot.mongodb.service.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    // @Scheduled(cron = "0 00 03 * * ?") // 3 Am everyday
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
            String link2;

            for (Element e : elementData) {
                // Go to the 1st div element and look at its text (in terms of js, value)
                String rowName = e.firstElementSibling().selectFirst("div").text();
                // Check if it is the right row we are looking for
                if (thailandDataRequiredTitle.contains(rowName)) {
                    // Obtain the link for first column
                    link2 = e.getElementsByIndexEquals(1).select("a").attr("abs:href");
//                    Obtain the link for the second column
                    link = e.getElementsByIndexEquals(2).select("a").attr("abs:href");
                    thailandLinks.put(rowName, link);
                    thailandLinks.put(rowName+" FirstCol", link2);
                    links.add(link);
                    links.add(link2);
                }
            }
            System.out.println(thailandLinks);
            for (Map.Entry<String, String> entry : thailandLinks.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value.contains("T02_01_01")) {
                    try {
                        if (value.contains("-1")) {
                            ThailandCrudeOilProductionScraper crudeOilProductionExcelScraper = new ThailandCrudeOilProductionScraper(value, key);
                            dataObjects.addAll(crudeOilProductionExcelScraper.scrapeThailand());
                        } else {
                            ThailandCrudeOilProductionScraperFirstCol crudeOilProductionExcelScraperFirstCol = new ThailandCrudeOilProductionScraperFirstCol(value, key);
                            dataObjects.addAll(crudeOilProductionExcelScraperFirstCol.scrapeThailand());
                        }
//                        System.out.println("Crude Oil Production Scraper Called");

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }

                }
                if (value.contains("T02_01_02")) {
                    System.out.println(value);
                    try {
                        if (value.contains("-1")) {
                            ThailandCondensateProductionScraper condensateProductionExcelScraper = new ThailandCondensateProductionScraper(value, key);
                            dataObjects.addAll(condensateProductionExcelScraper.scrapeThailand());
                        } else {
                            ThailandCondensateProductionScraperFirstCol condensateProductionExcelScraperFirstCol = new ThailandCondensateProductionScraperFirstCol(value, key);
                            dataObjects.addAll(condensateProductionExcelScraperFirstCol.scrapeThailand());

                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                if (value.contains("T02_01_03")) {
                    try {
                        if (value.contains("-1")) {
                            ThailandCrudeOilImportScraper crudeOilImportScraper = new ThailandCrudeOilImportScraper(value, key);
                            dataObjects.addAll(crudeOilImportScraper.scrapeThailand());
                        } else {
                            ThailandCrudeOilImportScraperFirstCol crudeOilImportScraperFirstCol = new ThailandCrudeOilImportScraperFirstCol(value, key);
                            dataObjects.addAll(crudeOilImportScraperFirstCol.scrapeThailand());
                        }
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
                        if (value.contains("-1")) {
                            ThailandPetroleumProductsProductionScraper petroleumProductsProductionScraper = new ThailandPetroleumProductsProductionScraper(value, key);
                            dataObjects.addAll(petroleumProductsProductionScraper.scrapeThailand());
                        } else {
                            ThailandPetroleumProductsProductionScraperFirstCol petroleumProductsProductionScraperFirstCol = new ThailandPetroleumProductsProductionScraperFirstCol(value, key);
                            dataObjects.addAll(petroleumProductsProductionScraperFirstCol.scrapeThailand());
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                if (value.contains("T02_03_04")) {
                    try {
                        if (value.contains("-1")) {
                            ThailandPetroleumProductsSalesScraper petroleumProductsSalesScraper = new ThailandPetroleumProductsSalesScraper(value, key);
                            dataObjects.addAll(petroleumProductsSalesScraper.scrapeThailand());
                        } else {
                            ThailandPetroleumProductsSalesScraperFirstCol petroleumProductsSalesScraperFirstCol = new ThailandPetroleumProductsSalesScraperFirstCol(value, key);
                            dataObjects.addAll(petroleumProductsSalesScraperFirstCol.scrapeThailand());
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }

//                EK ADDED HERE
//                Petroleum Products (Export & Import)
//                if (value.contains("T02_03_09")) {
                if ((value.contains("T02_03_09")) || (value.contains("T02_03_07"))) {
                    try {
                        if (value.contains("-1")) {
                            ThailandPetroleumProductsImportExportScraper petroleumProductsImportExportScraper = new ThailandPetroleumProductsImportExportScraper(value, key);
                            dataObjects.addAll(petroleumProductsImportExportScraper.scrapeThailand());
                        } else {
                            ThailandPetroleumProductsImportExportScraperFirstCol petroleumProductsImportExportScraperFirstCol = new ThailandPetroleumProductsImportExportScraperFirstCol(value, key);
                            dataObjects.addAll(petroleumProductsImportExportScraperFirstCol.scrapeThailand());
                        }
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
                        if (value.contains("-1")) {
                            ThailandPetroleumProductsNetExportScraper petroleumProductsNetExportScraper = new ThailandPetroleumProductsNetExportScraper(value, key);
                            dataObjects.addAll(petroleumProductsNetExportScraper.scrapeThailand());
                        } else {
                            ThailandPetroleumProductsNetExportScraperFirstCol petroleumProductsNetExportScraperFirstCol = new ThailandPetroleumProductsNetExportScraperFirstCol(value, key);
                            dataObjects.addAll(petroleumProductsNetExportScraperFirstCol.scrapeThailand());
                        }

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }

            }
//            thailandService.saveListThailand(dataObjects);
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


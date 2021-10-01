package com.OOP.springboot.mongodb.service;

import com.OOP.springboot.mongodb.service.utils.ChinaPageScraper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class CrawlerService {

    private final List<String> links;
    private final List<String> thailandDataRequiredTitle = new ArrayList<>(Arrays.asList(
            "Table 2.1-1: Production of Crude Oil",
            "Table 2.1-2: Production of Condensate",
            "Table 2.1-4: Quantity and Value of Petroleum Products Import",
            "Table 2.1-5: Quantity and Value of Petroleum Products Export",
            "Table 2.2-2: Material Intake",
            "Table 2.3-2: Production of Petroleum Products (Barrel/Day)"
    ));
    private final List<String> chinaDataRequiredTitle = new ArrayList<>(Arrays.asList(
            "（13）Major Export Commodities in Quantity and Value",
            "（14）Major Import Commodities in Quantity and Value"
    ));

    public CrawlerService(List<String> links) {
        this.links = new ArrayList<>();
    }

    // Thailand Web Scraping Service
    @Scheduled(cron = "0 00 03 * * ?") // 3 Am everyday
    public List<String> scrapeThailand() {
        String URL = "http://www.eppo.go.th/index.php/en/en-energystatistics/petroleum-statistic";
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
                    link = e.selectFirst("a").absUrl("href");
                    // Returning purpose
                    links.add(link);

                    // To obtain the raw bytes of the excel file from the link
                    byte[] bytes = Jsoup.connect(link)
                        .header("Accept-Encoding", "xls")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                            .referrer("http://www.eppo.go.th/index.php/en/en-energystatistics/petroleum-statistic")
                            .ignoreContentType(true)
                            .maxBodySize(0)
                            .timeout(600000)
                            .execute()
                            .bodyAsBytes();
                    try {
                        // Name of the of the file - taken from the website
                        String savedFileName = rowName.substring(13);

                        if (savedFileName.contains("/")) {
                            savedFileName = savedFileName.replace("/", " per ");
                        }
                        savedFileName = savedFileName.concat(".xls");
//                            if (!savedFileName.endsWith(".xls")) savedFileName.concat(".xls");

                        // To create the file (set in the excel_files folder)
                        FileOutputStream fos = new FileOutputStream("./excel_files/" + savedFileName);
                        fos.write(bytes);
                        fos.close();

                        System.out.println(savedFileName + " has been downloaded.");

                        // TODO: To complete the excel reading
                        double current_year = Calendar.getInstance().get(Calendar.YEAR);
                        FileInputStream excel_file = new FileInputStream("./excel_files/" + savedFileName);
                        Workbook wb = new HSSFWorkbook(excel_file);
                        Sheet sheet = wb.getSheetAt(0);
                        Iterator<Row> rows = sheet.iterator();
                        int dateColIndex = 0;
                        int totalColIndex = 0;
                        boolean found = false;
                        double ytd_total = 0;
                        boolean current_year_section = false;

                        Outer:
                        while (rows.hasNext()) {
                            Row currentRow = rows.next();

                            if (!current_year_section && currentRow.getCell(dateColIndex) != null) {
                                if (currentRow.getCell(dateColIndex).getCellType() == CellType.STRING &&
                                        currentRow.getCell(dateColIndex).getStringCellValue().trim().equals("2021")) {
                                    current_year_section = true;
                                }
                                else if (currentRow.getCell(dateColIndex).getCellType() == CellType.NUMERIC &&
                                        currentRow.getCell(dateColIndex).getNumericCellValue() == current_year) {
                                    current_year_section = true;
                                }
                            }

                            if (!found) {
                                Iterator<Cell> cellsInRow = currentRow.iterator();

                                // To find the "Total" column index in the excel file
                                while (cellsInRow.hasNext()) {
                                    Cell currentCell = cellsInRow.next();
                                    if (currentCell.getCellType() == CellType.STRING && currentCell.getStringCellValue().equals("Total")) {
                                        totalColIndex = currentCell.getColumnIndex();
                                        found = true;
                                        continue Outer;
                                    }
                                }
                            }
                            // Checking if the current row is "YTD"
                            else if (current_year_section && currentRow.getCell(dateColIndex).getCellType() == CellType.STRING &&
                                    currentRow.getCell(dateColIndex).getStringCellValue().trim().equals("YTD")) {
                                ytd_total = currentRow.getCell(totalColIndex).getNumericCellValue();
                                links.add(ytd_total + "");
                            }
                        }

                        // Close the workbook and stream
                        wb.close();
                        excel_file.close();

                        // Delete the files after reading it
//                        File f = new File("./excel_files/" + savedFileName);
//                        if (f.delete()) {
//                            System.out.println("Successful");
//                        }

                    } catch (IOException err) { // if file/link failed to be found, it will throw a checked error
                        System.err.println("Could not read the file at '" + link);
                        System.err.println("System error message: " + err.getMessage());
                    }
                }
            }
        } catch (IOException e) { // Same as the above - if URL cannot be found
            System.err.println("For '" + URL + "': " + e.getMessage());
        }

        return links;
    }


    //  China Web scraping service
//    @Scheduled(cron = "0 00 03 * * ?") // 3 Am everyday
    public List<Map<String, String>> scrapeChina() {

        String URL = "http://english.customs.gov.cn/statics/report/monthly.html";

        // Initialize list
        List<Map<String, String>> dataObjects = new ArrayList<>();

        try {
            Document document = Jsoup.connect(URL).get(); // fetch HTML code
            Elements elementData = document.select("table tr td"); // select elements which contain links
            String link; // store link when looping through anchor tags
            boolean isRequired = false; // Check if next td is part of required data

            for (Element e : elementData) {
                String title = e.text().trim();
                if (chinaDataRequiredTitle.contains(title)) {
                    isRequired = true;
                    continue;
                }
                // TODO Extract links for all the months
                if (isRequired) {
                    /* retrieve the link of the latest available month */
                    link = e.select("a").last().absUrl("href");
                    links.add(link);
                    isRequired = false;
                }
            }
            /* Retrieve data from the links extracted from the main page */
            for (String url: links){
                ChinaPageScraper scraper = ChinaPageScraper.getInstance(url);
                dataObjects.addAll(scraper.extractData());
            }
        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        }
        return dataObjects;
    }
}


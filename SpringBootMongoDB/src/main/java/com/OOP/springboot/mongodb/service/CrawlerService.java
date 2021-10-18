package com.OOP.springboot.mongodb.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
//            "Table 2.1-1: Production of Crude Oil",
//            "Table 2.1-2: Production of Condensate",
//            "Table 2.1-4: Quantity and Value of Petroleum Products Import",
//            "Table 2.1-5: Quantity and Value of Petroleum Products Export",
//            "Table 2.2-2: Material Intake",
//            "Table 2.3-2: Production of Petroleum Products (Barrel/Day)",
            "Table 2.3-9: Export of Petroleum Products (Barrel/Day)"
    ));
    private final List<String> chinaDataRequiredTitle = new ArrayList<>(Arrays.asList(
            "（13）Major Export Commodities in Quantity and Value",
            "（14）Major Import Commodities in Quantity and Value"
    ));
    /* Required fields for China */
    ArrayList<String> chinaRequiredImports = new ArrayList<>(Arrays.asList(
            "Crude petroleum oils",
            "Naphtha",
            "Aviation kerosene",
            "Natural gases"
    ));
    ArrayList<String> chinaRequiredExports = new ArrayList<>(Arrays.asList(
            "Gasoline",
            "Aviation kerosene",
            "Diesel oil"
    ));
    public CrawlerService(List<String> links) {
        this.links = new ArrayList<>();
    }

    // Thailand Web Scraping Service
    public List<String> scrapeThailand(String URL) {

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
//                    link = e.selectFirst("a").absUrl("href");
                    link = e.getElementsByIndexEquals(2).select("a").attr("abs:href");

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
                        ///////////////////////////////////////////
                        //////////////EXCEL INFO!!!////////////////
                        ///////////////////////////////////////////
                        List<String> colData = Arrays.asList("Month", "gasolineTotal", "gasolineReg", "gasolinePremium", "Kerosene", "dieselTotal", "dieselHSD", "dieselLSD", "JP", "fuelOil", "LPG", "Total");

                        int currentYear = 0;
                        int rowCount = sheet.getPhysicalNumberOfRows();
                        System.out.println("Row Count Total: " + rowCount);
                        int lastRow = 0;

                        for (int r=rowCount; r >= 0; r--) {
                            Row row = sheet.getRow(r);
                            if (row != null) {
                                Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                if (cell.getStringCellValue().equals("YTD")) {
                                    lastRow = r;
                                    break;
                                }
                            }
                        }

//                        Chunks of year-data existing in the sheet (15: 12 months + YTD + 2 header rows)
                        int chunksToLoop = rowCount/16;
//                        Starting row to evaluate latest year data
                        int latestChunk = lastRow - 15;

                        currentYear = (int) sheet.getRow(latestChunk).getCell(0).getNumericCellValue();
                        System.out.println("Current Year:" + currentYear);
                        System.out.println("The data is updated to Year " + (chunksToLoop + 1989));
                        System.out.println("Last row is :" + lastRow);
                        System.out.println("Row Count: " + rowCount);

//                        Looping each row in the chunk, 1 for each month + YTD (SINGLE YEAR CHUNK)
                        for (int i=0; i < 13; i ++) {
//                            Add 3 because of the 3 header rows
                            int currentRow = latestChunk + 3 + i;
//                            Looping each column of each row
                            for (int col=0; col < colData.size(); col++) {
                                Cell currentCell = sheet.getRow(currentRow).getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                System.out.println(currentCell);
                            }
                        }

                        for (int chunksLooped = 0; chunksLooped < chunksToLoop; chunksLooped++) {
                            System.out.println(chunksLooped + 1989);
                        }

                        System.out.println(lastRow);
                        int newLastRow = lastRow - 16;
                        int newLatestChunk = newLastRow-15;
                        System.out.println(lastRow-16);

                        // Close the workbook and stream
                        wb.close();
                        excel_file.close();

    //                        // Delete the files after reading it
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
}


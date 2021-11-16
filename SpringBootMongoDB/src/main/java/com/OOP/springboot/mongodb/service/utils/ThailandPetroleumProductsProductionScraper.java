package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ThailandPetroleumProductsProductionScraper{
    private String URL;
    private String rowName;
    private static final String[] monthHeaders = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "YTD"
    };

    public ThailandPetroleumProductsProductionScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        // Initialize list
        List<Map<String,String>> dataObjects = new ArrayList<>();
        List<String> tableHeaders = new ArrayList<String>();
        Map<String, String> extractedData = null;
        List<String> colData = Arrays.asList("Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel LSD", "JP", "Fuel Oil", "LPG", "Total");
        String productType = "production";

        try {
            byte[] bytes = Jsoup.connect(URL)
                    .header("Accept-Encoding", "xls")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .referrer("http://www.eppo.go.th/index.php/en/en-energystatistics/petroleum-statistic")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .timeout(600000)
                    .execute()
                    .bodyAsBytes();

            try {
                // Name of the file - taken from the website
                String savedFileName = rowName.substring(13);

                if (savedFileName.contains("/")) {
                    savedFileName = savedFileName.replace("/", " per ");
                }
                savedFileName = savedFileName.concat(".xls");

                // To create the file (set in the excel_files folder)
                FileOutputStream fos = new FileOutputStream("./excel_files/" + savedFileName);
                fos.write(bytes);
                fos.close();

                System.out.println(savedFileName + " has been downloaded.");

                // TODO: To complete the excel reading
                FileInputStream excel_file = new FileInputStream("./excel_files/" + savedFileName);
                Workbook wb = new HSSFWorkbook(excel_file);
                Sheet sheet = wb.getSheetAt(0);

                int rowTotal = 4;
                rowTotal = ThailandPetroleumProductsScraperParent.getTotalNumRows(sheet, rowTotal);
                String year = null;
                int bottomCell = rowTotal;
                int latestFourYear = bottomCell - (4*16);
                for (int yearRow = latestFourYear; yearRow < rowTotal; yearRow+= 16) {
                    Row currRow = sheet.getRow(yearRow);
                    Cell yearCell = currRow.getCell(0);
                    switch(yearCell.getCellType()) {
                        case BLANK:
                            break;
                        case NUMERIC:
                            year = (int)yearCell.getNumericCellValue() + "";
                            break;
                        case STRING:
                            year = yearCell.getStringCellValue();
                            break;
                    }

                    for (int a = 1; a<= colData.size(); a++) {
                        String product = colData.get(a-1);

                        for (int b = 1; b < 14; b++) {
                            extractedData = ThailandPetroleumProductsScraperParent.extractData(sheet, year, productType, product, yearRow, a, b);
                            dataObjects.add(extractedData);
                        }
                    }
                }

                // Close the workbook and stream
                wb.close();
                excel_file.close();

                // Delete the files after reading it
                File f = new File("./excel_files/" + savedFileName);
                if (f.delete()) {
                    System.out.println("Successful");
                }


            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return dataObjects;
    }
}

package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ThailandPetroleumProductsImportExportScraperFirstCol {
    private String URL;
    private String rowName;

    public ThailandPetroleumProductsImportExportScraperFirstCol(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Map<String, String> extractedData;
        List<String> colData;
        String productType;
        List<String> rowsToRead = Arrays.asList("Placeholder");
        if (rowName.contains("Import")) {
            productType = "import";
            rowsToRead = Arrays.asList("GASOLINE", " REGULAR", " PREMIUM", " BASE ULG", "KEROSENE", "DIESEL ", " HSD", " LSD", "J.P.    ", "FUEL OIL", "LPG", "TOTAL");
            colData = Arrays.asList("Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Gasoline Base ULG", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel LSD", "JP", "Fuel Oil", "LPG", "Total");
        } else {
            productType = "export";
            rowsToRead = Arrays.asList("GASOLINE", " REGULAR", " PREMIUM", "KEROSENE", "DIESEL ", " HSD", " LSD", "J.P.    ", "FUEL OIL", "LPG", "TOTAL");
            colData = Arrays.asList("Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel LSD", "JP", "Fuel Oil", "LPG", "Total");
        }

        try {
            // To obtain the raw bytes of the excel file from the link
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
                //Get name of the file from the website
                String savedFileName = rowName.substring(13);

                if (savedFileName.contains("/")) {
                    savedFileName = savedFileName.replace("/", " per ");
                }
                savedFileName = savedFileName.concat(".xls");

//              Create the file in the excel folder
                FileOutputStream fos = new FileOutputStream("./excel_files/" + savedFileName);
                fos.write(bytes);
                fos.close();

                System.out.println(savedFileName + " has been downloaded.");
                FileInputStream excel_file = new FileInputStream("./excel_files/" + savedFileName);
                Workbook wb = new HSSFWorkbook(excel_file);
                Sheet sheet = wb.getSheetAt(0);

                int rowCount = sheet.getPhysicalNumberOfRows();
                int startRow = 0;
                int monthSplitNum = 0;
                String currentYear = "";

                // Get currentYear, StartRow and monthSplitNum
                int[] currYearStartRowMonthSplitNum = ImportExportRows.getCurrentYearStartRowMonthSplitNum(sheet, rowCount, 4, "ENERGY", "GASOLINE");
                currentYear = currYearStartRowMonthSplitNum[0] + "";
                startRow = currYearStartRowMonthSplitNum[1];
                monthSplitNum = currYearStartRowMonthSplitNum[2];

                int rowsToCount = 24;
                int startCol = 10;

                dataObjects = ImportExportRows.inputData(sheet, rowName, productType, currentYear, startRow, rowsToCount, startCol, monthSplitNum);


//                Close workbook and stream
                wb.close();
                excel_file.close();

//                Delete excel file after reading
                File f = new File("./excel_files/" + savedFileName);
                if (f.delete()) {
                    System.out.println("Successful");
                }

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return dataObjects;
    }
}

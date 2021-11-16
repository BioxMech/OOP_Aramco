package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class ThailandCrudeOilProductionScraperFirstCol {
    private String URL;
    private String rowName;

    public ThailandCrudeOilProductionScraperFirstCol(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Map<String, String> extractedData;
        String productType;
        String commodity;
        List<String> monthsConvert = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "YTD");
        productType = "production";
        commodity = "Crude Oil";

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

//                System.out.println(savedFileName + " has been downloaded.");
                FileInputStream excel_file = new FileInputStream("./excel_files/" + savedFileName);
                Workbook wb = new HSSFWorkbook(excel_file);
                Sheet sheet = wb.getSheetAt(0);

                int rowCount = sheet.getPhysicalNumberOfRows();
                int lastRow;
                int startRow;
                int titleRowNum;
                String region = "";

                lastRow = VerticalDataRows.getLastRow(sheet, rowCount, "YTD");
                startRow = VerticalDataRows.getStartRow(sheet, rowCount);
                titleRowNum = VerticalDataRows.getTitleRow(sheet, rowCount, "Date");
                String currentYear = sheet.getRow(startRow).getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                dataObjects = VerticalDataRows.inputData(sheet, productType, commodity, currentYear, startRow, lastRow, titleRowNum, 12, "region", "KB/D");

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
            System.err.println(e.getMessage());
        }

        return dataObjects;
    }
}

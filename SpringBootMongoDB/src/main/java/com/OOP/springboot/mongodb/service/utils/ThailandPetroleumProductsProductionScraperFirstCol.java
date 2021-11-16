package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ThailandPetroleumProductsProductionScraperFirstCol {
    private String URL;
    private String rowName;

    public ThailandPetroleumProductsProductionScraperFirstCol(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Map<String, String> extractedData;
        List<String> colData;
        String productType;
        List<String> rowsToRead = Arrays.asList("Placeholder");
        productType = "production";

//        Diesel has 2 trailing spaces
        rowsToRead = Arrays.asList("GASOLINE", " REGULAR", " PREMIUM", "KEROSENE", "DIESEL  ", " HSD", " LSD", "J.P.    ", "FUEL OIL", "LPG", "TOTAL");
        colData = Arrays.asList("Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel LSD", "JP", "Fuel Oil", "LPG", "Total");
//        System.out.println(colData.get(7));
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
                int startRow = 0;
                int monthSplitNum = 0;
                String currentYear = "";

                for (int r=0; r < rowCount; r++) {
                    Row row = sheet.getRow(r);
                    if (row!= null) {
                        Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (cell.getStringCellValue().equals("ENERGY")) {
                            String monthSplitString = sheet.getRow(r).getCell(4).getStringCellValue().substring(0,2);
                            currentYear = sheet.getRow(r).getCell(10).getStringCellValue();
                            monthSplitNum = Integer.parseInt(monthSplitString.trim());
                            continue;
                        }

                        if (cell.getStringCellValue().equals("GASOLINE")) {
                            startRow = r;
                            break;
                        }
                    }
                }
                int rowsToCount = 25;
                int startCol = 10;
                int rowTitleCount = -1;

                for (int i=0; i < rowsToCount ; i++) {
                    String cellTitle = sheet.getRow(startRow + i).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    if (rowsToRead.contains(cellTitle)) {
                        rowTitleCount++;

                        for (int j=0; j < monthSplitNum; j++) {
                            Cell cellToRead = sheet.getRow(startRow + i).getCell(startCol + j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            double cellValue = cellToRead.getNumericCellValue();
                            double toSave = cellValue / 1000;
                            extractedData = new HashMap<>();
                            extractedData.put("year", currentYear + "");
                            extractedData.put("type", productType);
                            extractedData.put("commodity", colData.get(rowTitleCount));
                            extractedData.put("unit", "Kilobarrels/day");
                            extractedData.put("month", (j + 1) + "");
                            if (toSave != 0.0) {
                                extractedData.put("quantity", String.format("%.4f", toSave));
                            } else {
                                extractedData.put("quantity", "0");
                            }
                            dataObjects.add(extractedData);
                        }
                    }
                }

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

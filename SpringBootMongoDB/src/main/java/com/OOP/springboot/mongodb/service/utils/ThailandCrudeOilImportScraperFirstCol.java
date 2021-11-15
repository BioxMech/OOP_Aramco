package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ThailandCrudeOilImportScraperFirstCol {
    private String URL;
    private String rowName;

    public ThailandCrudeOilImportScraperFirstCol(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Map<String, String> extractedData;
        String productType;
        String commodity;
        UnitConverter unitConverter = new UnitConverter();
        List<String> monthsConvert = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "YTD");
        List<String> regionConvert = Arrays.asList("Middle East", "Far East", "Others", "Total");
        productType = "import";
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
                int lastRow = 0;
                int startRow = 0;
                int titleRowNum = 0;
                String region = "";

                for (int r=rowCount; r >= 0; r--) {
                    Row row = sheet.getRow(r);
                    if (row != null) {
                        Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (cell.getCellType() == CellType.STRING) {
                            if (cell.getStringCellValue().trim().equals("YTD")) {
                                lastRow = r;
                            }

                            if (cell.getStringCellValue().trim().equals("JAN")) {
                                startRow = r-1;
                                break;
                            }
                        }
                    }
                }

                for (int i=0; i < 10; i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Cell cell = sheet.getRow(i).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (cell.getCellType() == CellType.STRING) {
                            if (cell.getStringCellValue().trim().equals("DATE")) {
                                titleRowNum = i;
                            }
                        }
                    }
                }

                // We convert it from numeric as 2.1.3's year is written in Numeric, whereas others are String
                int currentYearInt = (int) sheet.getRow(startRow).getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();
                String currentYear = currentYearInt + "";

                for (int i=startRow+1; i < lastRow+1; i++) {
                    int monthArrayIndex = (i-startRow-1);

                    for (int j=1; j < (4+1) ; j++) {
                        Cell cell = sheet.getRow(i).getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//                        System.out.println(cell);

                        String toSave = unitConverter.convertToKbd(String.valueOf(cell.getNumericCellValue()),1000, "KL", commodity, currentYear, monthsConvert.get(monthArrayIndex));

//                        System.out.print(toSave + " ");
                        region = regionConvert.get(j-1);
                        extractedData = new HashMap<>();
                        extractedData.put("year", currentYear);
                        extractedData.put("type", productType);
                        extractedData.put("commodity", commodity);
                        extractedData.put("unit", "Kilobarrels/day");
                        extractedData.put("continent", region);

                        if (i == lastRow) {
                            extractedData.put("month", "YTD");
                        } else {
                            extractedData.put("month", monthsConvert.get(monthArrayIndex));

                        }

                        if (!toSave.equals("0.0")) {
                            extractedData.put("quantity", toSave);
                        } else {
                            extractedData.put("quantity", "0");
                        }
                        dataObjects.add(extractedData);
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
            System.err.println(e.getMessage());
        }

        return dataObjects;
    }
}

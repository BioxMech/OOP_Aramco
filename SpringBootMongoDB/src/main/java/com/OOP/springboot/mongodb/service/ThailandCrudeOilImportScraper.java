package com.OOP.springboot.mongodb.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class ThailandCrudeOilImportScraper {
    private String URL;
    private String rowName;
    private static final String[] monthHeaders = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "TOTAL"
    };

    public ThailandCrudeOilImportScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, List<Map<String, Map<String, Integer>>>>> scrapeThailand() {
        List<Map<String, List<Map<String, Map<String, Integer>>>>> dataObjects = new ArrayList<>();
//        List<Map<String, String>> headerObjects = new ArrayList<>();
        Map<String, List<Map<String, Map<String, Integer>>>> currYearData = new HashMap<>();
        String unit;
        String productType = "import";
        String commodityType = "Crude Oil";



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

                FileInputStream excel_file = new FileInputStream("./excel_files/" + savedFileName);
                Workbook wb = new HSSFWorkbook(excel_file);
                Sheet sheet = wb.getSheetAt(0);

                // obtaining the unit
                Row unitRow = sheet.getRow((2));
                Cell unitCell = unitRow.getCell(0);
                unit = unitCell.getStringCellValue().split(":")[1].trim();
//                System.out.println(unit);

                // loop to get the number of rows containing necessary data
                int rowTotal = 4;
                while (true) {
                    Row currRow = sheet.getRow(rowTotal);
                    Cell firstCol = currRow.getCell(0);
                    if (firstCol.getCellType() == CellType.STRING && firstCol.getStringCellValue().contains("SOURCE")) {
                        break;
                    }
                    else {
                        rowTotal++;
                    }
                }
                rowTotal--;
                System.out.println(rowTotal);
                String year = null;
                int bottomCell = rowTotal;
                for (int yearRow = 4; yearRow < 5; yearRow+=6) {
                    Row currRow = sheet.getRow((yearRow));
                    Cell yearCell = currRow.getCell(0);
                    if (yearCell.getCellType() == CellType.BLANK) {
                        break;
                    }
                    switch(yearCell.getCellType()) {
                        case NUMERIC:
                            year = (int)yearCell.getNumericCellValue() + "";
                            System.out.println(year);
                            break;
                        case STRING:
                            year = yearCell.getStringCellValue();
                            System.out.println(year);
                            break;
                    }

                    // for loop to get each row for the continents
                    for (int dataRow = 2; dataRow < 6; dataRow++) {
                        Row nextRow = sheet.getRow((yearRow+dataRow));
                        String continent = nextRow.getCell(0).getStringCellValue();
                        System.out.println(continent);
                        Map<String, String> extractedData = new HashMap<>();
                        extractedData.put("year", year);
                        extractedData.put("type", productType);
                        extractedData.put("commodity", commodityType);
                        extractedData.put("unit", unit);
                        extractedData.put("continent", continent);

                        // for loop to loop through each column for the row
                        for (int col = 1; col < 14; col++) {
                            System.out.println(monthHeaders[col-1]);
                            extractedData.put("month", monthHeaders[col-1]);
                            Cell cell = nextRow.getCell(col);
                            switch(cell.getCellType()) {
                                case BLANK:
                                    extractedData.put(monthHeaders[col-1], "0");
                                    System.out.println(0);
                                    break;
                                case NUMERIC:
                                    extractedData.put(monthHeaders[col-1], String.valueOf(cell.getNumericCellValue()));
                                    System.out.println(cell.getNumericCellValue());
                                    break;
                                case STRING:
                                    extractedData.put(monthHeaders[col-1], cell.getStringCellValue());
                                    System.out.println(cell.getStringCellValue());
                                    break;
                            }
                        }
                        System.out.println(extractedData);

                    }

                }





            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        }
        return dataObjects;
    }

}

package com.OOP.springboot.mongodb.service.utils;

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
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };

    public ThailandCrudeOilImportScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String,String>> scrapeThailand() {
        List<Map<String,String>> dataObjects = new ArrayList<>();
//        List<Map<String, String>> headerObjects = new ArrayList<>();
        String unit;
        String productType = "import";
        String commodityType = "Crude Oil";
        UnitConverter unitConverter = new UnitConverter();

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
                int latestFourYear = bottomCell - (4*6);
                for (int yearRow = latestFourYear; yearRow < rowTotal; yearRow+=6) {
                    Row currRow = sheet.getRow((yearRow));
                    Cell yearCell = currRow.getCell(0);
                    switch(yearCell.getCellType()) {
                        case BLANK:
                            break;
                        case NUMERIC:
                            year = (int)yearCell.getNumericCellValue() + "";
//                            System.out.println(year);
                            break;
                        case STRING:
                            year = yearCell.getStringCellValue();
//                            System.out.println(year);
                            break;
                    }

                    // for loop to get each row for the continents
                    for (int dataRow = 2; dataRow < 6; dataRow++) {
                        Row nextRow = sheet.getRow((yearRow+dataRow));
                        String Oldcontinent = nextRow.getCell(0).getStringCellValue();
                        String[] splitted = Oldcontinent.toLowerCase().split(" ");
                        for (int i = 0; i < splitted.length; i++) {
                            String A = splitted[i];
                            splitted[i] = A.toUpperCase().charAt(0)+A.substring(1);
                        }
                        String continent = String.join(" ", splitted);

                        // for loop to loop through each column for the row
                        for (int col = 1; col < 13; col++) {
//                            System.out.println(monthHeaders[col-1]);
                            Map<String, String> extractedData = new HashMap<>();
                            extractedData.put("year", year);
                            extractedData.put("type", productType);
                            extractedData.put("commodity", commodityType);
                            extractedData.put("unit", "Kilobarrels/day");
                            extractedData.put("continent", continent);
                            extractedData.put("month", String.valueOf(col));
                            Cell cell = nextRow.getCell(col);
                            switch(cell.getCellType()) {
                                case BLANK:
                                    extractedData.put(monthHeaders[col-1], "0");
//                                    System.out.println(0);
                                    break;
                                case NUMERIC:
//                                    extractedData.put(monthHeaders[col-1], String.valueOf(cell.getNumericCellValue()));
                                    String qty = unitConverter.convertToKbd(String.valueOf(cell.getNumericCellValue()),1000, "KL", commodityType, year, col+"");
                                    extractedData.put("quantity", qty);

//                                    System.out.println(cell.getNumericCellValue());
                                    break;
                                case STRING:
                                    extractedData.put(monthHeaders[col-1], cell.getStringCellValue());
//                                    System.out.println(cell.getStringCellValue());
                                    break;
                            }
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
                System.err.println("For '" + URL + "': " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        }
        return dataObjects;
    }

}

package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ThailandCondensateProductionScraper {
    private String URL;
    private String rowName;
    private static final String[] monthHeaders = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "YTD"
    };
    private static final String[] thirtyOneDays = {"JAN","MAR","MAY","JUL", "AUG","OCT","DEC"};

    public ThailandCondensateProductionScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String,String>> scrapeThailand() {
        List<Map<String,String>> dataObjects = new ArrayList<>();
        List<String> tableHeaders = new ArrayList<>();
        Map<String, String> extractedData = null;
        String unit;
        String productType = "production";
        String commodityType = "Condensate";
        int tableRows;

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

                //obtaining the unit
                Row unitRow = sheet.getRow(2);
                Cell unitCell = unitRow.getCell(0);
                unit = unitCell.getStringCellValue().split(":")[1].trim();

                // Map out the headers in the excel file
                int i = 1;
                Row headerRow = sheet.getRow(4);
                Iterator<Cell> headerCellIterator= headerRow.cellIterator();
                while(headerCellIterator.hasNext()) {
                    Cell cell = headerCellIterator.next();
                    switch (cell.getCellType()) {
                        case STRING:
                            if (cell.getStringCellValue().equals("MONTH"))
                                continue;
                            tableHeaders.add(cell.getStringCellValue());
                            ++i;
                            break;
                    }
                }
//                System.out.println(tableHeaders);

                // Extracting the data
                int rowTotal = 5;
                while (true) {
                    Row currRow = sheet.getRow(rowTotal);
                    Cell yearCell = currRow.getCell(0);
                    if (yearCell.getCellType() == CellType.BLANK) {
                        break;
                    }
                    else {
                        rowTotal+=1;
                    }
                }
//                System.out.println(rowTotal);
                int bottomCell = rowTotal;
                int latestFourYear = bottomCell - (4*14);
                for (int yearRow = latestFourYear; yearRow < rowTotal; yearRow+=14) {
                    Row currRow = sheet.getRow(yearRow);
                    Cell yearCell = currRow.getCell(0);

                    String year = null;
                    switch (yearCell.getCellType()) {
                        case BLANK:
                            break;
                        case NUMERIC:
                            year = ((int)yearCell.getNumericCellValue()) + "";
//                            System.out.println(year);
                            break;
                        case STRING:
                            year = yearCell.getStringCellValue();
//                            System.out.println(year);
                            break;
                    }

                    for (int a = 1; a <= tableHeaders.size(); a++) {
                        String region = tableHeaders.get(a-1);

                        for (int b = 1; b < 14; b++) {
                            String month = monthHeaders[b-1];
                            extractedData = new HashMap<>();
                            extractedData.put("year", year);
                            extractedData.put("type", productType);
                            extractedData.put("commodity", commodityType);
                            extractedData.put("unit", "Kilobarrels/day");
                            extractedData.put("region", region);
                            extractedData.put("month", b+"");
                            Row row = sheet.getRow(yearRow+b);
                            Cell cell = row.getCell(a);
                            switch(cell.getCellType()) {
                                case BLANK:
//                                    System.out.println("0");
                                    extractedData.put("quantity", "0");
                                case NUMERIC:
//                                    System.out.println((int)cell.getNumericCellValue()+"");
                                    extractedData.put("quantity", String.format("%.4f",cell.getNumericCellValue()/1000));
//                                case STRING:
//                                    System.out.println(cell.getStringCellValue());
                            }
                            dataObjects.add(extractedData);
                        }
//                        System.out.println(extractedData);
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


            } catch (IOException err) {
                System.err.println("System error message: " + err.getMessage());
            }

        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        }

//        System.out.println(dataObjects);
        return dataObjects;
    }
}

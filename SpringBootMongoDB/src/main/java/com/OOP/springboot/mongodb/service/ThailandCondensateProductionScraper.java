package com.OOP.springboot.mongodb.service;

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

    public ThailandCondensateProductionScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, List<Map<String, Map<String, Integer>>>>> scrapeThailand() {
        List<Map<String, List<Map<String, Map<String, Integer>>>>> dataObjects = new ArrayList<>();
        List<Map<String, String>> headerObjects = new ArrayList<>();
        Map<String, List<Map<String, Map<String, Integer>>>> currYearData = new HashMap<>();
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
//                            if (!savedFileName.endsWith(".xls")) savedFileName.concat(".xls");

                // To create the file (set in the excel_files folder)
                FileOutputStream fos = new FileOutputStream("./excel_files/" + savedFileName);
                fos.write(bytes);
                fos.close();

                System.out.println(savedFileName + " has been downloaded.");

                // TODO: To complete the excel reading
                FileInputStream excel_file = new FileInputStream("./excel_files/" + savedFileName);
                Workbook wb = new HSSFWorkbook(excel_file);
                Sheet sheet = wb.getSheetAt(0);

                // Map out the headers in the excel file
                Map<String, String> headerObjectsIndiv = new HashMap<>();
                int i = 1;
                Row headerRow = sheet.getRow(4);
                Iterator<Cell> headerCellIterator= headerRow.cellIterator();
                while(headerCellIterator.hasNext()) {
                    Cell cell = headerCellIterator.next();
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            headerObjectsIndiv.put(i+"", cell.getNumericCellValue() +"");
                            ++i;
                            break;
                        case STRING:
                            if (cell.getStringCellValue().equals("MONTH"))
                                continue;
                            headerObjectsIndiv.put(i+"", cell.getStringCellValue());
                            ++i;
                            break;
                    }
                }
                headerObjects.add(headerObjectsIndiv);
//                System.out.println(headerObjects);

                // Extracting the data
//                int rowTotal = sheet.getLastRowNum();
                int rowTotal = 5;
//                System.out.println(rowTotal);

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
                System.out.println(rowTotal);
                int bottomCell = rowTotal;
                int latestThreeYear = bottomCell - (3*14);
                for (int yearRow = latestThreeYear; yearRow < rowTotal; yearRow+=14) {
                    Row currRow = sheet.getRow(yearRow);
                    Cell yearCell = currRow.getCell(0);
                    List<Map<String, Map<String, Integer>>> dataToAdd = new ArrayList<>();

                    if (yearCell.getCellType() == CellType.BLANK) {
                        break;
                    }
                    String year = null;
                    switch (yearCell.getCellType()) {
                        case NUMERIC:
                            year = ((int)yearCell.getNumericCellValue()) + "";
//                            System.out.println(year);
                            break;
                        case STRING:
                            year = yearCell.getStringCellValue();
//                            System.out.println(year);
                            break;
                    }

                    // for loop to loop through and obtain the months and YTD
                    Map<String, Map<String, Integer>> currMonthData = null;
                    for (int dataRow = 1; dataRow < 14; dataRow++) {
                        currMonthData = new HashMap<>();
                        Row nextRow = sheet.getRow((yearRow+dataRow));
                        String month = nextRow.getCell(0).getStringCellValue().trim();
//                                System.out.println(month);

                        // for loop to loop through the cells in the row
                        Map<String, Integer> regionalData = new HashMap<>();
                        for (int a = 1; a < 8; a++) {
                            String header = headerObjects.get(0).get(a+"");
                            Cell cell = nextRow.getCell(a);
                            if (cell.getCellType() == CellType.BLANK) {
                                regionalData.put(header, 0);
                            }
                            switch (cell.getCellType()) {
                                case NUMERIC:
                                    regionalData.put(header, (int) cell.getNumericCellValue());
                                    break;
                                case STRING:
                                    regionalData.put(header, Integer.parseInt(cell.getStringCellValue()));
                                    break;
                            }
                        }
//                                System.out.println(regionalData);
                        currMonthData.put(month, regionalData);
//                                System.out.println("currMonthData"+currMonthData);
                        dataToAdd.add(currMonthData);
                    }

                    currYearData.put(year, dataToAdd);
//                            System.out.println(currYearData);
                    dataObjects.add(currYearData);

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

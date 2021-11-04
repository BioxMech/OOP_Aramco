package com.OOP.springboot.mongodb.service.utils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

// Scraper for File T02_03_11
public class ThailandPetroleumProductsNetExportScraper {
    private String URL;
    private String rowName;

    public ThailandPetroleumProductsNetExportScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String, String>> dataObjects = new ArrayList<>();
        List<String> colData = Arrays.asList("Month", "Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Gasoline Base ULG", "Kerosene", "Diesel", "JP", "Fuel Oil", "LPG", "Total");
        String productType = "net export";
        String commodityType;

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

                int currentYear = 0;
                int rowCount = sheet.getPhysicalNumberOfRows();
//                System.out.println("Row Count Total: " + rowCount);
                int lastRow = 0;

                for (int r=rowCount; r >= 0; r--) {
                    Row row = sheet.getRow(r);
                    if (row != null) {
                        Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (cell.getStringCellValue().equals("TOTAL")) {
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
//                System.out.println("Current Year:" + currentYear);
//                System.out.println("The data is updated to Year " + (chunksToLoop + 1986));
//                System.out.println("Last row is :" + lastRow);
//                System.out.println("Row Count: " + rowCount);

                // chunksToLoop = 3;
                for (int chunksLooped = 0; chunksLooped < chunksToLoop; chunksLooped++) {
                    currentYear = (int) sheet.getRow(latestChunk).getCell(0).getNumericCellValue();
                    // System.out.println("BREAK");
//                            System.out.println(chunksLooped + 1986);
                    //                        Looping each row in the chunk, 1 for each month + YTD (SINGLE YEAR CHUNK)
                    for (int i=0; i < 13; i ++) {
//                            Add 3 because of the 3 header rows
                        int currentRow = latestChunk + 3 + i;
//                            Looping each column of each row
                        for (int col=1; col < colData.size(); col++) {
//                            System.out.println(currentYear);
                            Map<String, String> extractedData = new HashMap<>();
                            extractedData.put("year", currentYear+"");
                            extractedData.put("type", productType);
                            commodityType = colData.get(col);
//                            System.out.print("Commodity: " + commodityType + " ");
                            extractedData.put("commodity", commodityType);
                            extractedData.put("unit", "Kilobarrels/day");
                            extractedData.put("month", (i+1)+"");
                            double toSave = 0.0;
                            Cell currentCell = sheet.getRow(currentRow).getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            if ((currentCell.getCellType() == CellType.NUMERIC) && (currentCell.getNumericCellValue() != 0.0)) {
                                toSave = currentCell.getNumericCellValue()/1000;
                                extractedData.put("quantity", String.format("%.4f", toSave));
//                                System.out.print(toSave + " ");
                            } else {
                                extractedData.put("quantity", "0");
                            }
                            dataObjects.add(extractedData);
                        }
//                        System.out.println();
                    }
                    latestChunk -= 16;
                }

//                Close the workbook and end stream
                wb.close();
                excel_file.close();

//                Delete the file after reading
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

package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

// Scraper for File T02_03_09
public class ThailandPetroleumProductsImportExportScraper {
    private String URL;
    private String rowName;

    public ThailandPetroleumProductsImportExportScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Map<String, String> extractedData;
        List<String> colData;
        String productType;
        if (rowName.contains("Import")) {
            productType = "import";
            colData = Arrays.asList("Month", "Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Gasoline Base ULG", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel LSD", "JP", "Fuel Oil", "LPG", "YTD");
        } else {
            productType = "export";
            colData = Arrays.asList("Month", "Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel LSD", "JP", "Fuel Oil", "LPG", "Total");
        }


        try{
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

                int currentYear;
                int rowCount = sheet.getPhysicalNumberOfRows();
                int lastRow = 0;

                for (int r=rowCount; r >= 0; r--) {
                    Row row = sheet.getRow(r);
                    if (row != null) {
                        Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (cell.getStringCellValue().equals("YTD")) {
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

                for (int chunksLooped = 0; chunksLooped < chunksToLoop; chunksLooped++) {
//                  Looping each row in the chunk, 1 for each month + YTD (SINGLE YEAR CHUNK)
                    for (int i=0; i < 13; i ++) {
//                            Add 3 because of the 3 header rows
                        int currentRow = latestChunk + 3 + i;
//                      Looping each column of each row
                        for (int col=1; col < colData.size(); col++) {
                            extractedData = new HashMap<>();
                            String product = colData.get(col);
                            extractedData.put("year", currentYear+"");
                            extractedData.put("type", productType);
                            extractedData.put("commodity", product);
                            extractedData.put("unit", "Kilobarrels/day");
                            extractedData.put("month", (i+1)+"");

                            Cell currentCell = sheet.getRow(currentRow).getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            double toSave = 0;

                            if ((currentCell.getCellType() == CellType.NUMERIC) && (currentCell != null)) {
                                toSave = currentCell.getNumericCellValue()/1000;
//                              Adding the data into the hashmap
                                extractedData.put("quantity", String.format("%.4f",toSave));
                            }
                            else {
                                extractedData.put("quantity", "0");
                            }
                            dataObjects.add(extractedData);
                        }
                    }
                    latestChunk -= 16;
                    currentYear -= 1;
                }

//                Close workbook and stream
                wb.close();
                excel_file.close();

                File f= new File("./excel_files/" + savedFileName);
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

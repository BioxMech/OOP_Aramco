package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

// Scraper for File T02_01_04
public class ThailandCrudeOilPetroleumProductsQtyValImportExportScraper {
    private String URL;
    private String rowName;

    public ThailandCrudeOilPetroleumProductsQtyValImportExportScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String, String>> dataObjects = new ArrayList<>();
        String productType;
        List<String> colData = Arrays.asList("", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        if (rowName.contains("Import")) {
            productType = "import";
        } else {
            productType = "export";
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

//                Start of extracting data from excel sheet
                int currentYear = 0;
                int rowCount = sheet.getPhysicalNumberOfRows();
                Cell testLastRow = sheet.getRow(rowCount).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                int lastRow = 0;

                for (int r=rowCount; r >= 0; r--) {
                    Row row = sheet.getRow(r);

                    if (row == null) {
                        row = sheet.createRow(r);
                    }

                    if (row != null) {
                        Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (cell.getStringCellValue().equals(" -PRICE  ($/BBL)")) {
                            lastRow = r;
                            Cell celltest = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            break;
                        }
                    }
                }

//                        Chunks of year-data existing in the sheet (15: 12 months + YTD + 2 header rows)
                int chunksToLoop = rowCount/29;
//                        Starting row to evaluate latest year data
                int latestChunk = lastRow - 28;

                currentYear = (int) sheet.getRow(latestChunk).getCell(0).getNumericCellValue();

                List<Integer> importantRows = Arrays.asList(5,6,7,12,13,14,19,20,21,26,27,28);
                List<Integer> titleRows = Arrays.asList(2,9,16,23);
                String titleToSave = "";
                String product = "";

                for (int loopCount=0; loopCount < 4; loopCount++) {
                    if (loopCount > 0) {
                        latestChunk -= 29;
                    }
                    Cell yearChunkCell = sheet.getRow(latestChunk).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    double yearChunkNum = yearChunkCell.getNumericCellValue();
                    //Looping each row in the chunk, 1 for Each Row
                    for (int i=0; i < 27; i ++) {
//                            Add 2 because of the 2 header rows
                        int currentRow = latestChunk + 2 + i;

                        Cell rowTitleCell = sheet.getRow(currentRow).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String rowTitle = rowTitleCell.getStringCellValue();
                        if (rowTitle.contains("CRUDE OIL")) {
                            product = "Crude Oil";
                        } else if (rowTitle.contains("PETROLEUM PRODUCTS")) {
                            product = "Petroleum Products";
                        } else if (rowTitle.contains("OTHERS")) {
                            product = "Others";
                        } else if (rowTitle.contains("TOTAL PETROLEUM")) {
                            product = "Total Petroleum";
                        }

                        //Looping each column of each row
                        if (rowTitle.contains("BBL/D")) {
                            for (int col=1; col < colData.size(); col++) {
                                Map<String, String> extractedData = new HashMap<>();
                                extractedData.put("year", (int)yearChunkNum+"");
                                extractedData.put("type", productType);
                                extractedData.put("commodity", product);
                                extractedData.put("unit", "Kilobarrels/day");
                                extractedData.put("month", colData.get(col));
                                double toSave = 0;
                                Cell currentCell = sheet.getRow(currentRow).getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                if ((currentCell.getCellType() == CellType.NUMERIC) && (currentCell.getNumericCellValue() != 0.0)) {
                                    toSave = currentCell.getNumericCellValue()/1000;
                                    extractedData.put("quantity", String.format("%.4f", toSave));
                                } else {
                                    extractedData.put("quantity", "0");
                                }
                                dataObjects.add(extractedData);
                            }
                        }
                    }
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

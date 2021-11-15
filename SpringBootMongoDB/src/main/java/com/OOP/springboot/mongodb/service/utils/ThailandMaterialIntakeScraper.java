package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ThailandMaterialIntakeScraper {
    private String URL;
    private String rowName;

    public ThailandMaterialIntakeScraper(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String,String>> dataObjects = new ArrayList<>();
        List<String> tableHeaders = new ArrayList<String>();
        Map<String, String> extractedData = null;
        List<String> colData = Arrays.asList("Fang", "Thai Oil", "Bangchak", "Esso","TPI_IRPC",
                "RRC_PTTAR_PTTGC", "SPRC", "RPC", "Total");
        String productType = "Material Intake";

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

                int rowTotal = 3;
                while (true) {
                    Row currRow = sheet.getRow(rowTotal);
                    if (currRow == null) {
                        break;
                    }
                    Cell firstCol = currRow.getCell(0);
                    if (firstCol.getCellType() == CellType.BLANK) {
                        break;
                    }
                    else {
                        rowTotal++;
                    }
                }
                String year = null;
                int latestFourYear;
                int bottomCell = rowTotal;
                if (savedFileName.contains("FirstCol")) {
                    latestFourYear = 3;
                } else {
                    latestFourYear = bottomCell - (4*15);
                }
                for (int yearRow = latestFourYear; yearRow < rowTotal; yearRow+=15) {
                    Row currRow = sheet.getRow(yearRow);
                    Cell yearCell = currRow.getCell(0);
                    switch(yearCell.getCellType()) {
                        case BLANK:
                            break;
                        case NUMERIC:
                            year = (int)yearCell.getNumericCellValue() + "";
                            break;
                        case STRING:
                            year = yearCell.getStringCellValue();
                            break;
                    }

                    OUTER: for (int a = 1; a<= colData.size(); a++) {
                        String product = colData.get(a-1);
                        if (product.equals("RPC")) {
                            continue OUTER;
                        }
                         for (int b = 1; b < 14; b++) {
                            extractedData = new HashMap<>();
                            extractedData.put("year", year);
                            extractedData.put("type", productType);
                            extractedData.put("refinery", product);
                            extractedData.put("commodity", "Material");
                            extractedData.put("unit", "Kilobarrels/day");
                            if (b == 13){
                                extractedData.put("month", "YTD");
                            } else {
                                extractedData.put("month", b+"");
                            }
                            Row row = sheet.getRow(yearRow+1+b);
                            Cell cell;
                            if (savedFileName.contains("FirstCol") && product.equals("Total")) {
                                cell = row.getCell(8);
                            } else {
                                cell = row.getCell(a);
                            }
                            switch(cell.getCellType()) {
                                case BLANK:
                                    extractedData.put("quantity", "0");
                                    break;
                                case NUMERIC:
                                    extractedData.put("quantity", String.format("%.4f",cell.getNumericCellValue()/1000));
                                    break;
                                case STRING:
                                    extractedData.put("quantity", String.format("%.4f",Double.parseDouble(cell.getStringCellValue())/1000 ));
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
                System.err.println(e.getMessage());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return dataObjects;
    }
}

package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ThailandCrudeOilPetroleumProductsQtyValImportExportScraperFirstCol {
    private String URL;
    private String rowName;
    private int currYear = Calendar.getInstance().get(Calendar.YEAR);

    public ThailandCrudeOilPetroleumProductsQtyValImportExportScraperFirstCol(String URL, String rowName) {
        this.URL = URL;
        this.rowName = rowName;
    }

    public List<Map<String, String>> scrapeThailand() {
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Map<String, String> extractedData;
        String productType;
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
                int rowTotal = 3;
                rowTotal = ThailandPetroleumProductsScraperParent.getTotalNumRows(sheet, rowTotal);
                rowTotal--;
                int headerRowNum = 3;

                Row headerRow = sheet.getRow(headerRowNum);
                Iterator<Cell> headerRowIterator = headerRow.iterator();
                int startIndex2021 = 0;
                while (headerRowIterator.hasNext()) {
                    Cell currentCell = headerRowIterator.next();
                    if (currentCell.getCellType() == CellType.STRING && currentCell.getStringCellValue().equals(currYear+"")) {
                        break;
                    }
                    startIndex2021++;
                }
                int endIndex2021 = startIndex2021;
                while (headerRowIterator.hasNext()) {
                    Cell currentCell = headerRowIterator.next();
                    if (currentCell.getCellType() == CellType.STRING && currentCell.getStringCellValue().contains("GROWTH")) {
                        break;
                    }
                    endIndex2021++;
                }

                int productsRowStart = 5;
                String product;
                for (int i = productsRowStart; i <11; i+=7) {
                    Row productRow = sheet.getRow(i);
                    String rowTitle = productRow.getCell(0).getStringCellValue();
                    Row dataRow = sheet.getRow(i+3);
                    int month = 1;
                    for (int j = startIndex2021; j <= endIndex2021; j++) {
                        extractedData = new HashMap<>();
                        extractedData.put("year", currYear+"");
                        extractedData.put("type", productType);
                        extractedData.put("commodity", "Crude Oil");
                        extractedData.put("unit", "Kilobarrels/day");
                        extractedData.put("month", month+"");
                        month++;
                        Cell dataCell = dataRow.getCell(j);
                        switch(dataCell.getCellType()) {
                            case BLANK:
                                extractedData.put("quantity", "0");
                            case NUMERIC:
                                extractedData.put("quantity", String.format("%.4f",dataCell.getNumericCellValue()/1000));
                        }
                        dataObjects.add(extractedData);
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

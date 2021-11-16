package com.OOP.springboot.mongodb.service.utils;

import java.util.*;
import org.apache.poi.ss.usermodel.*;


public class ThailandPetroleumProductsScraperParent {

    public static int getTotalNumRows(Sheet sheet, int rowTotal) {
        while (true) {
            Row currRow = sheet.getRow(rowTotal);
            if (currRow == null) {
                break;
            }
            Cell firstCol = currRow.getCell(0);
            if (firstCol.getCellType() == CellType.STRING && (firstCol.getStringCellValue().contains("Source") || firstCol.getStringCellValue().contains("REMARKS"))) {
                break;
            }
            else {
                rowTotal++;
            }
        }
        return rowTotal;
    }

    public static Map<String, String> extractData(Sheet sheet, String year, String productType, String product, int yearRow, int a, int b) {
        String unit = "Kilobarrels/day";
        Map<String, String> extractedData = new HashMap<>();
        extractedData.put("year", year);
        extractedData.put("type", productType);
        extractedData.put("commodity", product);
        extractedData.put("unit", unit);
        if (b == 13){
            if (productType.equals("production")) {
                extractedData.put("month", "YTD");
            } else if (productType.equals("sales")) {
                extractedData.put("month", "Total");
            }
        } else {
            extractedData.put("month", b+"");
        }
        Row row = sheet.getRow(yearRow+2+b);
        Cell cell = row.getCell(a);
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
        return extractedData;
    }
}

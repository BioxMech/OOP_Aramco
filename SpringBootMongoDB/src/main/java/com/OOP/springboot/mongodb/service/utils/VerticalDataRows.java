package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.ss.usermodel.*;
import java.util.*;

public class VerticalDataRows {

//   Keyword depends on file if they name it YTD or TOTAL
    public static int getLastRow(Sheet sheet, int rowCount, String keyword) {
        int lastRow = 0;

        for (int r=rowCount; r >= 0; r--) {
//            System.out.println("Getting last row...");
            Row row = sheet.getRow(r);
            if (row != null) {
                Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//                System.out.println(cell);
                if (cell.getCellType() == CellType.STRING) {
                    if (cell.getStringCellValue().trim().equals(keyword)) {
                         lastRow = r;
                         break;
                    }
                }
            }
        }
        return lastRow;
    }

//    Assumes that the keyword is JAN for month data
    public static int getStartRow(Sheet sheet, int rowCount) {
//        System.out.println("Getting start row...");
        int startRow = 0;

        for (int r=rowCount; r >= 0; r--) {
            Row row = sheet.getRow(r);
            if (row != null) {
                Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cell.getCellType() == CellType.STRING) {
                    if (cell.getStringCellValue().trim().equals("JAN")) {
                        startRow = r-1;
                        break;
                    }
                }
            }
        }

        return startRow;
    }

    public static int getTitleRow(Sheet sheet, int rowCount, String titleKeyword) {
        int titleRowNum = 0;
        for (int i=0; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = sheet.getRow(i).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cell.getCellType() == CellType.STRING) {
                    if (cell.getStringCellValue().trim().equals(titleKeyword)) {
                       titleRowNum = i;
                       break;
                    }
                }
            }
        }
        return titleRowNum;
    }

    public static List<Map<String, String>> inputData(Sheet sheet, String productType, String commodity, String currentYear, int startRow, int lastRow, int titleRowNum, int dataColumnCount, String uniqueData, String dataUnitType) {
        UnitConverter unitConverter = new UnitConverter();
        Map<String, String> extractedData;
        List<String> monthsConvert = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "YTD");
        List<Map<String, String>> dataObjects = new ArrayList<>();
        for (int i=startRow+1; i < lastRow+1; i++) {
            int monthArrayIndex = (i-startRow-1);

            for (int j=1; j < (dataColumnCount + 1); j++) {
                extractedData = new HashMap<>();
                double toSave = 0.0;
                String toSaveString = "";
                Cell cell = sheet.getRow(i).getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                double cellValue = cell.getNumericCellValue();
                String continent;

                // Standard data to input
                extractedData.put("year", currentYear);
                extractedData.put("type", productType);
                extractedData.put("commodity", commodity);
                extractedData.put("unit", "Kilobarrels/day");

                if (dataUnitType.equals("KB/D")) {
                    toSave = cellValue/1000;
                } else if (dataUnitType.equals("Million Litres")){ // Use UnitConverter from Mil.Litres to KB/D
                    toSaveString = unitConverter.convertToKbd(String.valueOf(cell.getNumericCellValue()), 1000, "KL", commodity, currentYear, monthsConvert.get(monthArrayIndex));
                }

                if (uniqueData.equals("region")) {
                    String region = sheet.getRow(titleRowNum).getCell(j).getStringCellValue().trim();
                    extractedData.put("region", region);
                } else if (uniqueData.equals("continent")) {
                    List<String> continentConvert = Arrays.asList("Middle East", "Far East", "Others", "Total");
                    continent = sheet.getRow(titleRowNum).getCell(j).getStringCellValue().trim();
                    extractedData.put("continent", continent);
                }

                if (i==lastRow) {
                    extractedData.put("month", "YTD");
                } else {
                    extractedData.put("month", monthsConvert.get(monthArrayIndex));
                }

                if (toSave != 0.0) {
                    extractedData.put("quantity", String.format("%.4f", toSave));
                } else if (!toSaveString.equals("")) {
                    extractedData.put("quantity", toSaveString);
                } else {
                    extractedData.put("quantity", "0");
                }

                dataObjects.add(extractedData);
            }
        } return dataObjects;
    }
}

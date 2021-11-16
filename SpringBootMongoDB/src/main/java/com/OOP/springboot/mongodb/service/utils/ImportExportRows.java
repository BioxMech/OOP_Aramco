package com.OOP.springboot.mongodb.service.utils;

import org.apache.poi.ss.usermodel.*;
import java.util.*;

public class ImportExportRows {

    public static int[] getCurrentYearStartRowMonthSplitNum(Sheet sheet, int rowCount, int monthSplitCol, String titleKeyword, String dataStartKeyword) {
        int currentYear = 0;
        int startRow = 0;
        int monthSplitNum = 0;

        for (int r=0; r < rowCount; r++) {
            Row row = sheet.getRow(r);
            if (row!= null) {
                Cell cell = sheet.getRow(r).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cell.getCellType() == CellType.STRING) {
                    if (cell.getStringCellValue().equals(titleKeyword)) {
                        String monthSplitString = sheet.getRow(r).getCell(monthSplitCol).getStringCellValue().substring(0,2);

                        //Default is 10, as 10th col is where the year data starts
                        currentYear = Integer.parseInt(sheet.getRow(r).getCell(10).getStringCellValue());
                        monthSplitNum = Integer.parseInt(monthSplitString.trim());
                        continue;
                    }

                    if (cell.getStringCellValue().equals(dataStartKeyword)) {
                        startRow = r;
                        break;
                    }

                }
            }
        }
        int[] output = {currentYear, startRow, monthSplitNum};
        return output;
    }

    public static  List<Map<String, String>> inputData(Sheet sheet, String rowName, String currentYear, int startRow, int rowsToCount, int startCol, int monthSplitNum) {
        List<String> rowsToRead = Arrays.asList("rowsToReadPlaceholder");
        List<String> colData = Arrays.asList("colDataPlaceholder");
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Map<String, String> extractedData;
        String productType = "";
        int rowsTitleCount = -1;

        if (rowName.contains("Import")) {
            productType = "import";
            rowsToRead = Arrays.asList("GASOLINE", "REGULAR", "PREMIUM", "BASE ULG", "KEROSENE", "DIESEL", "HSD", "LSD", "J.P.", "FUEL OIL", "LPG", "TOTAL");
            colData = Arrays.asList("Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Gasoline Base ULG", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel LSD", "JP", "Fuel Oil", "LPG", "Total");

        } else if (rowName.contains("Export")) {
            if (rowName.contains("Net")) {
                //        Diesel has 2 trailing spaces
                productType = "net export";
                rowsToRead = Arrays.asList("GASOLINE", "REGULAR", "PREMIUM", "BASE ULG", "KEROSENE", "DIESEL", "HSD", "HSD 0.5 %", "LSD", "J.P.", "FUEL OIL", "LPG", "TOTAL");
                colData = Arrays.asList("Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Gasoline Base ULG", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel HSD 0.5%", "Diesel LSD", "JP", "Fuel Oil", "LPG", "Total");
            } else {
                productType = "export";
                rowsToRead = Arrays.asList("GASOLINE", " REGULAR", " PREMIUM", "KEROSENE", "DIESEL ", " HSD", " LSD", "J.P.    ", "FUEL OIL", "LPG", "TOTAL");
                colData = Arrays.asList("Gasoline Total", "Gasoline Regular", "Gasoline Premium", "Kerosene", "Diesel Total", "Diesel HSD", "Diesel LSD", "JP", "Fuel Oil", "LPG", "Total");
            }
        }


        for (int i=0; i < rowsToCount; i++) {
            String cellTitle = sheet.getRow(startRow + i).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

            if (rowsToRead.contains(cellTitle.trim())) {
                rowsTitleCount++;

                for (int j=0; j < monthSplitNum; j ++) {
                    Cell cellToRead = sheet.getRow(startRow + i).getCell(startCol + j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    double cellValue = cellToRead.getNumericCellValue();
                    double toSave = cellValue / 1000;

                    extractedData = new HashMap<>();
                    extractedData.put("year", currentYear);
                    extractedData.put("type", productType);
                    extractedData.put("commodity", colData.get(rowsTitleCount));
                    extractedData.put("unit", "Kilobarrels/day");
                    extractedData.put("month", (j +1) + "");

                    if ( toSave != 0.0 ) {
                        extractedData.put("quantity", String.format("%.4f", toSave));
                    } else {
                        extractedData.put("quantity", "0");
                    }
                    dataObjects.add(extractedData);
                }
            }
        } return dataObjects;
    }
}

package com.OOP.springboot.mongodb.service.utils;

import com.OOP.springboot.mongodb.model.China;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.*;

@Component
public class ChinaExcel {
    @Autowired
    S3Upload s3Upload;

    private ArrayList<List<China>> chinaList;
    private String commodity;
    private String[] yearHeader = {"Year", "Month", "Value", "Quantity", "% Change Value", "% Change Quantity"};
    private String[] keroseneHeader = {"Year", "Month", "Value (Import)", "Value (Export)", "Quantity (Import)", "Quantity(Export)", "% Change Value (Import)", "% Change Value (Export)", "% Change Quantity (Import)", "% Change Quantity (Export)"};


    public void saveAllByYear(ArrayList<List<China>> chinaList, String commodity) {

        // Create the excel file. Naming convention --> Commodity
        String filepath = "./excel_files/China/" + commodity + ".csv";

        try {
            // Create File
            FileWriter file = new FileWriter(filepath);

            // Creation of file is successful
            // Create writer
            CSVWriter writer = new CSVWriter(file);

            // Write Header
            writer.writeNext(yearHeader);

            // Loop through all the years to create excel for each year
            for (List<China> yearlyChinaList : chinaList) {

                // Check whether commodity has one or two types
                // If 1 only --> single function
                // If both import and export --> merged sheet
                // Loop through every object in year and add it to the data in CSV format
                //{"Year", "Month", "Value", "Quantity", "% Change Value", "% Change Quantity"}
                for (China chinaObj : yearlyChinaList) {
                    String[] individualData = new String[6];
                    individualData[0] = chinaObj.getYear();
                    individualData[1] = chinaObj.getMonth();
                    individualData[2] = chinaObj.getValue();
                    individualData[3] = chinaObj.getQuantity();
                    individualData[4] = chinaObj.getPercent_change_value();
                    individualData[5] = chinaObj.getPercent_change_quantity();

                    // Write into the csv file
                    writer.writeNext(individualData);
                }
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("Error occurred in creation of file");
        }

//        uploadToS3(filepath);

        // File Name
        String curr = java.time.LocalDate.now().toString();
        String fileName = curr + "/" + "China/" + commodity;

        //TODO save this filename into the database

//        S3Upload newUpload = new S3Upload(filepath, "csv", fileName);
//        newUpload.uploadFile();
        s3Upload.uploadFile(filepath, "csv", fileName);

    }

    public void saveKerosene(ArrayList<List<China>> chinaList, String commodity){

        // Process the data
        // List to store data
        List<Map<String, List<China>>> keroseneData = new ArrayList<>();

        // Loop through and save according to the structure
        for (List<China> yearlyChinaList: chinaList){

            Map<String,List<China>> yearlyHashMap = new HashMap<>();

            for (China chinaObj: yearlyChinaList){

                // To store the import and export object
                List<China> yearAndMonthList = new ArrayList<>();

                // To retrieve the year month and save to hashmap
                String year = chinaObj.getYear();
                String month = chinaObj.getMonth();
                String id = chinaObj.getId();
                String key = year + month;

                yearAndMonthList.add(chinaObj);

                for (China chinaObj2: yearlyChinaList){

                    // Retrieve year and month
                    String year2 = chinaObj2.getYear();
                    String month2 = chinaObj2.getMonth();

                    // Check if this object is the same year and month as object 1 and not equal to object 1
                    if ((year2 + month2).equals(key) && !(chinaObj2.getId().equals(id))) {
                        yearAndMonthList.add(chinaObj2);
                    }
                }
                yearlyHashMap.put(year + "," + month, yearAndMonthList);
            }
            keroseneData.add(yearlyHashMap);
        }

        // Create the excel file. Naming convention --> Commodity
        String filepath = "./excel_files/China/" + commodity + ".csv";

        try {
            // Create File
            FileWriter file = new FileWriter(filepath);

            // Creation of file is successful
            // Create writer
            CSVWriter writer = new CSVWriter(file);

            // Write Header
            writer.writeNext(keroseneHeader);

            // Loop through the yearlyHashMap
            for (Map<String, List<China>> yearlyHashMap: keroseneData){

                // Loop through the hashmap
                for (String key: yearlyHashMap.keySet()){
                    String[] individualData = new String[10];
                    String[] yearMonth = key.split(",");
                    String year = yearMonth[0];
                    String month = yearMonth[1];

                    individualData[0] = year;
                    individualData[1] = month;

                    // Loop through the items to add the values to the individual data
                    for (China chinaObj: yearlyHashMap.get(key)){
                        // For export objects
                        if (chinaObj.getType().equals("export")) {
                            individualData[3] = chinaObj.getValue();
                            individualData[5] = chinaObj.getQuantity();
                            individualData[7] = chinaObj.getPercent_change_value();
                            individualData[9] = chinaObj.getPercent_change_quantity();
                        }
                        // For import objects
                        else if (chinaObj.getType().equals("import")){
                            individualData[2] = chinaObj.getValue();
                            individualData[4] = chinaObj.getQuantity();
                            individualData[6] = chinaObj.getPercent_change_value();
                            individualData[8] = chinaObj.getPercent_change_quantity();
                        }
                    }

                    writer.writeNext(individualData);

                }
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("Error occurred in creation of file");
        }

//        uploadToS3(filepath);

        // File Name
        String curr = java.time.LocalDate.now().toString();
        String fileName = curr + "/China/" + commodity;


        // Upload excel to S3
//        S3Upload newUpload = new S3Upload(filepath, "csv", fileName);
//        newUpload.uploadFile();
        s3Upload.uploadFile(filepath, "csv", fileName);

    }
}

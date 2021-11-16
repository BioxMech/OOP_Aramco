package com.OOP.springboot.mongodb.service.utils;

import com.OOP.springboot.mongodb.model.Thailand;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.*;

@Component
public class ThailandExcel {

    @Autowired
    S3Upload s3Upload;

    private ArrayList<List<Thailand>> thailandList;
    private String commodity;
    private String type;
    private String[] headers = {"year", "month", "commodity", "quantity", "unit", "region", "continent", "refinery"};
    private String commodityPath = null;
    private String fileName = null;
    private final String localPath = "./excel_files/Thailand/";
    private String curr = java.time.LocalDate.now().toString();



    public void saveAll(ArrayList<List<Thailand>> thailandList, String type, String commodity) {
        // Create the excel file. Naming convention --> Commodity
        fileName = String.join("",commodity.split(" ")) + type +".csv";;
        if (commodity.equals("Crude Oil") || commodity.equals("Condensate")) {
            commodityPath = commodity + "/";
        }
        else {
            commodityPath = "Petroleum Products/";
        }

        try {
            // Create File
            FileWriter file = new FileWriter(localPath+commodityPath+fileName);

            // Creation of file is successful
            // Create writer
            CSVWriter writer = new CSVWriter(file);
            // Write Header
            writer.writeNext(headers);

            for (List<Thailand> yearlyThailandList : thailandList) {
//                System.out.println(filepath);

                for (Thailand thailandObj : yearlyThailandList) {
                    String[] individualData = new String[8];
                    individualData[0] = thailandObj.getYear();
                    individualData[1] = thailandObj.getMonth();
                    individualData[2] = thailandObj.getCommodity();
                    individualData[3] = thailandObj.getQuantity();
                    individualData[4] = thailandObj.getUnit();
                    individualData[5] = thailandObj.getRegion();
                    individualData[6] = thailandObj.getContinent();
                    individualData[7] = thailandObj.getRefinery();

                    // Write into the csv file
                    writer.writeNext(individualData);
                }
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Parent folder most likely missing. Please check");
        }

        String s3FilePath = curr + "/" + "Thailand/" + commodityPath + fileName.split("[.]")[0];
        s3Upload.uploadFile(localPath+commodityPath+fileName, "csv", s3FilePath);
    }
}

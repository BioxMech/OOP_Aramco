package com.OOP.springboot.mongodb.service.utils;

import com.OOP.springboot.mongodb.model.Thailand;
import com.opencsv.CSVWriter;


import java.io.*;
import java.util.*;

public class ThailandExcel {
    private ArrayList<List<Thailand>> thailandList;
    private String commodity;
    private String type;
    private String[] headers = {"year", "month", "commodity", "quantity", "unit", "region", "continent", "refinery"};
    private String commodityPath = null;
    private String fileName = null;
    private final String localPath = "./excel_files/Thailand/";
    private String curr = java.time.LocalDate.now().toString();

    public ThailandExcel(ArrayList<List<Thailand>> thailandList, String type, String commodity) {
        this.thailandList = thailandList;
        this.commodity = commodity;
        this.type = type;
    }

    public void saveAll() {
        // Create the excel file. Naming convention --> Commodity
        if (commodity.equals("Crude Oil") || commodity.equals("Condensate")) {
            commodityPath = commodity + "/";
            fileName =  String.join("",commodity.split(" ")) + type +".csv";
        }
        else {
            commodityPath = "Petroleum Products/";
            fileName = String.join("",commodity.split(" ")) + type +".csv";;
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

        System.out.println(s3FilePath);
        S3Upload newUpload = new S3Upload(localPath+commodityPath+fileName, "csv", s3FilePath);
        newUpload.uploadFile();

    }
}

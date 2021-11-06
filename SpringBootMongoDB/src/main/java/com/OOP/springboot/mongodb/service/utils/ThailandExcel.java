package com.OOP.springboot.mongodb.service.utils;

import com.OOP.springboot.mongodb.model.Thailand;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.*;

public class ThailandExcel {
    private ArrayList<List<Thailand>> thailandList;
    private String commodity;
    private String[] headers = {"type", "commodity", "unit", "year", "region", "quantity", "month", "continent", "refinery"};

    public ThailandExcel(ArrayList<List<Thailand>> thailandList, String commodity) {
        this.thailandList = thailandList;
        this.commodity = commodity;
    }

    public void sayAllByYear() {
        // Create the excel file. Naming convention --> Commodity
        String filepath = "./excel_files/Thailand/" + commodity + ".csv";

        try {
            // Create File
            FileWriter file = new FileWriter(filepath);

            // Creation of file is successful
            // Create writer
            CSVWriter writer = new CSVWriter(file);
            // Write Header
            writer.writeNext(headers);

            for (List<Thailand> yearlyThailandList: thailandList) {
                System.out.println(filepath);
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Parent folder most likely missing. Please check");
        }



    }
}

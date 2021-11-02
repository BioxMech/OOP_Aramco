package com.OOP.springboot.mongodb.service.utils;

import com.OOP.springboot.mongodb.model.s3;
import com.OOP.springboot.mongodb.service.s3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class S3Upload {

    private String fileName;
    private String localFilePath;
    private String fileExtension;

    private String url = "https://jrqbtfbdg0.execute-api.us-east-1.amazonaws.com/beta/uploadfile";

    public S3Upload(String localFilePath, String fileExtension, String fileName){
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.localFilePath = localFilePath;
    }

    public void uploadFile(){

        try {

            // Retrieve file and create Base64 String
            File file = new File(localFilePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());

            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            RestTemplate restTemplate = new RestTemplate();

            Map<String,String> map = new HashMap<>();
            map.put("fileName", fileName);
            map.put("fileExtension", fileExtension);
            map.put("content", encodedString);

            ResponseEntity<Void> response = restTemplate.postForEntity(url, map, Void.class);

            if (response.getStatusCode() == HttpStatus.OK){
                System.out.println(response);
                System.out.println("Request Successful");
            } else {
                System.out.println("Request Failed");
            }




        } catch (IOException e) {
            System.out.println("Error occurred in uploading to S3");
        }

    }


}

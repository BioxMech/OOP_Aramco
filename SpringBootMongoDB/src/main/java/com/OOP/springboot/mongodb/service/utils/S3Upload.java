package com.OOP.springboot.mongodb.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Component
public class S3Upload {

    @Value("${app.S3URL}")
    private String S3URL;
    private String fileName;
    private String localFilePath;
    private String fileExtension;

    public void uploadFile(String localFilePath, String fileExtension, String fileName){

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

            ResponseEntity<Void> response = restTemplate.postForEntity(S3URL, map, Void.class);

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

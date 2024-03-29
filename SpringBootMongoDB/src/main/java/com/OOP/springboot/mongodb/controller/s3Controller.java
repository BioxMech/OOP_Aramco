package com.OOP.springboot.mongodb.controller;

import com.OOP.springboot.mongodb.message.ResponseMsg;
import com.OOP.springboot.mongodb.model.s3;
import com.OOP.springboot.mongodb.service.s3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;

@RestController
@RequestMapping("/api/s3")
public class s3Controller {

    @Autowired
    s3Service s3Service;

    @GetMapping("/retrieves3link/{country}/{commodity}")
    public ResponseEntity<ResponseMsg> retrieveS3Link(@PathVariable String country, @PathVariable String commodity){

        try {
            List<s3> result = s3Service.retrieveS3LinksByCountryAndCommodity(country, commodity);
            s3 latestS3 = result.get(0);
            String s3Link = latestS3.getS3Link();
            return new ResponseEntity<ResponseMsg>(new ResponseMsg("S3 Link found successfully", s3Link), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/retrieves3link/thailand/{commodity}")
    public ResponseEntity<ResponseMsg> retrieveS3Link( @PathVariable String commodity) {
        try {
            List<Map<String, String>> returnVal = new ArrayList<>();
            List<s3> result = s3Service.retrieveThailandS3LinksByCountryAndCommodity("Thailand", commodity);

            Map<String, String> temp = new HashMap<>();
            for (s3 ele: result) {
                String[] splitted = ele.getS3Link().split("[/]");
                String key = splitted[splitted.length-1].split("[.]")[0];
                temp.put(key, ele.getS3Link());
            }
            returnVal.add(temp);
            return new ResponseEntity<ResponseMsg>(new ResponseMsg("S3 Link found successfully", returnVal), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

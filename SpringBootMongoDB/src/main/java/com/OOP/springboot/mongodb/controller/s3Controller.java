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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/s3")
public class s3Controller {

    @Autowired
    s3Service s3Service;

    //TODO Check if this endpoint is needed. If needed, fix. Else, remove.
//    @GetMapping("/retrieves3link/{country}")
//    public ResponseEntity<ResponseMsg> retrieveS3Link(@PathVariable String country){
//        try {
//            System.out.println("HELLO");
//            List<s3> result = s3Service.retrieveS3LinksByCountry(country);
//
//            Map<String, String> s3Links = new HashMap<>();
//
//            for (s3 s3: result){
//                s3Links.put(s3.getCommodity(), s3.getS3Link());
//            }
//
//            return new ResponseEntity<ResponseMsg>(new ResponseMsg("S3 Links found successfully", s3Links), HttpStatus.OK);
//        } catch (Exception e){
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/retrieves3link/{country}/{commodity}")
    public ResponseEntity<ResponseMsg> retrieveS3Link(@PathVariable String country, @PathVariable String commodity){

        try {
            List<s3> result = s3Service.retrieveS3LinksByCountryAndCommodity(country, commodity);
            s3 latestS3 = result.get(0);
            String s3Link = latestS3.getS3Link();
            System.out.println(s3Link);
            return new ResponseEntity<ResponseMsg>(new ResponseMsg("S3 Link found successfully", s3Link), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

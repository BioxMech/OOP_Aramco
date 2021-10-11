package com.OOP.springboot.mongodb.controller;

import com.OOP.springboot.mongodb.model.China;
import com.OOP.springboot.mongodb.model.Customer;
import com.OOP.springboot.mongodb.repository.ChinaRepository;
import com.OOP.springboot.mongodb.repository.CustomerRepository;
import com.OOP.springboot.mongodb.service.ChinaService;
import com.OOP.springboot.mongodb.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/china")
public class ChinaController {
    @Autowired
    ChinaService chinaService;

    public String saveChina(China china) {
        try {
            China _china = chinaService.saveChina(china);
            return "Successfully uploaded one China data to MongoDB with id = " + _china.getId();
        }catch(Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<China>> getAllChina(HttpServletRequest request) {
        try {

            List<China> china = chinaService.retrieveAllChina();
            return new ResponseEntity<>(china, HttpStatus.OK);
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

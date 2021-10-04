package com.OOP.springboot.mongodb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.China;
import com.OOP.springboot.mongodb.repository.ChinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ChinaService {

    @Autowired
    ChinaRepository repo;

    public China saveChina(China china) { return repo.save(china); }

    public List<China> saveListChina(List<Map<String, String>> china) {
        List<China> writeData = new ArrayList<>();
        for (Map<String, String> data: china) {
            writeData.add(new China(data));
        }
        return repo.saveAll(writeData);
    }

    public List<China> retrieveAllChina(){
        return repo.findAll();
    }

    public void deleteAll() { repo.deleteAll(); }
}
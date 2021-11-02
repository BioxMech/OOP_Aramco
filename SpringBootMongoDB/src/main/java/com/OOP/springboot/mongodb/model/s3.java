package com.OOP.springboot.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "s3")
public class s3 {
    @Id
    private String id;
    private String commodity;
    private String country;
    private String s3Link;
    private String date;
    private String baseS3Link = "https://aramcobucket.s3.amazonaws.com/";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getS3Link() {
        return s3Link;
    }

    public void setS3Link(String s3Link) {
        this.s3Link = s3Link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @PersistenceConstructor
    public s3(String commodity, String country, String s3Link, String date) {
        this.commodity = commodity;
        this.country = country;
        if (s3Link.indexOf("http") != -1){
            this.s3Link = s3Link;
        } else {
            this.s3Link = this.baseS3Link + s3Link;
        }
        this.date = date;
    }

    public s3(String id, String commodity, String country, String s3Link, String date) {
        this.id = id;
        this.commodity = commodity;
        this.country = country;
        this.s3Link = s3Link;
        this.date = date;
    }

}

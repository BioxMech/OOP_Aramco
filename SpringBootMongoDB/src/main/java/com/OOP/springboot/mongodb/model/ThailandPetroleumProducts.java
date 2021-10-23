package com.OOP.springboot.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
@Document(collection = "ThailandPetroleumProducts")

public class ThailandPetroleumProducts {
    @Id
    private String id;
    private String type;
    private String commodity;
    private String unit;
    private String year;
    private String quantity;
    private String month;

    public ThailandPetroleumProducts(String id, String type, String commodity, String unit, String year, String quantity, String month) {
        this.id = id;
        this.type = type;
        this.commodity = commodity;
        this.unit = unit;
        this.year = year;
        this.quantity = quantity;
        this.month = month;
    }

    public ThailandPetroleumProducts(Map<String, String> dataArray) {
        this.id = dataArray.get("id");
        this.type = dataArray.get("type");
        this.commodity = dataArray.get("commodity");
        this.unit = dataArray.get("unit");
        this.year = dataArray.get("year");
        this.quantity = dataArray.get("quantity");
        this.month = dataArray.get("month");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}

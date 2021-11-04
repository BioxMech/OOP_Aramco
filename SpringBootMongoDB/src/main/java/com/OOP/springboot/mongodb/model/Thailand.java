package com.OOP.springboot.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document(collection = "Thailand")
public class Thailand {
    @Id
    private String id;
    private String type;
    private String commodity;
    private String unit;
    private String year;
    private String region;
    private String quantity;
    private String month;
    private String continent;
    private String refinery;

    public String getRefinery() {
        return refinery;
    }

    public void setRefinery(String refinery) {
        this.refinery = refinery;
    }

    @PersistenceConstructor
    public Thailand(String id, String type, String commodity, String unit, String year, String region, String quantity, String month, String continent, String refinery) {
        this.id = id;
        this.type = type;
        this.commodity = commodity;
        this.unit = unit;
        this.year = year;
        this.region = region;
        this.quantity = quantity;
        this.month = month;
        this.continent = continent;
        this.refinery = refinery;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public Thailand(Map<String, String> dataArray) {
        this.id = dataArray.get("id");
        this.type = dataArray.get("type");
        this.commodity = dataArray.get("commodity");
        this.unit = dataArray.get("unit");
        this.year = dataArray.get("year");
        this.region = dataArray.get("region");
        this.quantity = dataArray.get("quantity");
        this.month = dataArray.get("month");
        this.continent = dataArray.get("continent");
        this.refinery = dataArray.get("refinery");
    }


}

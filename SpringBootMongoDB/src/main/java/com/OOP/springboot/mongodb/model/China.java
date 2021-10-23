package com.OOP.springboot.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "China")
public class China {
    @Id
    private String id;
    private String type;
    private String commodity;
    private String unit;
    private String value;
    private String quantity;
    private String percent_change_value;
    private String percent_change_quantity;
    private String month;
    private String year;
    private String link;
    private String error;

    @PersistenceConstructor
    public China(String type, String commodity, String unit, String value, String quantity, String percent_change_value, String percent_change_quantity, String month, String year, String link, String error) {
        this.type = type;
        this.commodity = commodity;
        this.unit = unit;
        this.value = value;
        this.quantity = quantity;
        this.percent_change_value = percent_change_value;
        this.percent_change_quantity = percent_change_quantity;
        this.month = month;
        this.year = year;
        this.link = link;
        this.error = error;
    }

    public China(Map<String, String> data) {
        this.id = data.get("id");
        this.type = data.get("type");
        this.commodity = data.get("commodity");
        this.unit = data.get("unit");
        this.value = data.get("value");
        this.quantity = data.get("quantity");
        this.percent_change_value = data.get("percent_change_value");
        this.percent_change_quantity = data.get("percent_change_quantity");
        this.month = data.get("month");
        this.year = data.get("year");
        this.link = data.get("link");
        this.error = data.get("error");
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPercent_change_value() {
        return percent_change_value;
    }

    public void setPercent_change_value(String percent_change_value) {
        this.percent_change_value = percent_change_value;
    }

    public String getPercent_change_quantity() {
        return percent_change_quantity;
    }

    public void setPercent_change_quantity(String percent_change_quantity) {
        this.percent_change_quantity = percent_change_quantity;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}



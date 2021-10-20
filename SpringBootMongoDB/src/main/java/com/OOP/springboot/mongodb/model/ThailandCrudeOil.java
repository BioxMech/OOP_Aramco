package com.OOP.springboot.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
@Document(collection = "ThailandCrudeOil")

public class ThailandCrudeOil {
    @Id
    private String id;
    private String type;
    private String commodity;
    private String unit;
    private String year;
    private String region;
    private String JAN;
    private String FEB;
    private String MAR;
    private String APR;
    private String MAY;
    private String JUN;
    private String JUL;
    private String AUG;
    private String SEP;
    private String OCT;
    private String NOV;
    private String DEC;

    public ThailandCrudeOil(String id, String type, String commodity, String unit, String year, String region, String JAN, String FEB, String MAR, String APR, String MAY, String JUN, String JUL, String AUG, String SEP, String OCT, String NOV, String DEC) {
        this.id = id;
        this.type = type;
        this.commodity = commodity;
        this.unit = unit;
        this.year = year;
        this.region = region;
        this.JAN = JAN;
        this.FEB = FEB;
        this.MAR = MAR;
        this.APR = APR;
        this.MAY = MAY;
        this.JUN = JUN;
        this.JUL = JUL;
        this.AUG = AUG;
        this.SEP = SEP;
        this.OCT = OCT;
        this.NOV = NOV;
        this.DEC = DEC;
    }
    public ThailandCrudeOil(Map<String, String> dataArray) {
        this.id = dataArray.get("id");
        this.type = dataArray.get("type");
        this.commodity = dataArray.get("commodity");
        this.unit = dataArray.get("unit");
        this.year = dataArray.get("year");
        this.region = dataArray.get("region");
        this.JAN = dataArray.get("JAN");
        this.FEB = dataArray.get("FEB");
        this.MAR = dataArray.get("MAR");
        this.APR = dataArray.get("APR");
        this.MAY = dataArray.get("MAY");
        this.JUN = dataArray.get("JUN");
        this.JUL = dataArray.get("JUL");
        this.AUG = dataArray.get("AUG");
        this.SEP = dataArray.get("SEP");
        this.OCT = dataArray.get("OCT");
        this.NOV = dataArray.get("NOV");
        this.DEC = dataArray.get("DEC");
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

    public String getJAN() {
        return JAN;
    }

    public void setJAN(String JAN) {
        this.JAN = JAN;
    }

    public String getFEB() {
        return FEB;
    }

    public void setFEB(String FEB) {
        this.FEB = FEB;
    }

    public String getMAR() {
        return MAR;
    }

    public void setMAR(String MAR) {
        this.MAR = MAR;
    }

    public String getAPR() {
        return APR;
    }

    public void setAPR(String APR) {
        this.APR = APR;
    }

    public String getMAY() {
        return MAY;
    }

    public void setMAY(String MAY) {
        this.MAY = MAY;
    }

    public String getJUN() {
        return JUN;
    }

    public void setJUN(String JUN) {
        this.JUN = JUN;
    }

    public String getJUL() {
        return JUL;
    }

    public void setJUL(String JUL) {
        this.JUL = JUL;
    }

    public String getAUG() {
        return AUG;
    }

    public void setAUG(String AUG) {
        this.AUG = AUG;
    }

    public String getSEP() {
        return SEP;
    }

    public void setSEP(String SEP) {
        this.SEP = SEP;
    }

    public String getOCT() {
        return OCT;
    }

    public void setOCT(String OCT) {
        this.OCT = OCT;
    }

    public String getNOV() {
        return NOV;
    }

    public void setNOV(String NOV) {
        this.NOV = NOV;
    }

    public String getDEC() {
        return DEC;
    }

    public void setDEC(String DEC) {
        this.DEC = DEC;
    }
}

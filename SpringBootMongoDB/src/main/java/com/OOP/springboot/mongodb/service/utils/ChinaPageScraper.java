package com.OOP.springboot.mongodb.service.utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ChinaPageScraper {
    private Document doc;
    private String[] requiredCommodities;
    private String month;
    private Elements table;
    private int commodityIndex;
    private int unitIndex;
    private int quantityIndex;
    private int valueIndex;

    public ChinaPageScraper(Document doc, Elements header, String[] requiredCommodities) {
        this.doc = doc;
        this.month = getMonthFromHeader(header);
        this.table = doc.select("table");
        this.requiredCommodities = requiredCommodities;
        initializeIndices();
    }

    public static ChinaPageScraper getInstance(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements header = doc.select("div.atcl-ttl");
        String headerText = header.text();
        if (headerText.contains("Import")) {
            return new ChinaImportsPageScraper(doc, header);
        } else {
            return new ChinaExportsPageScraper(doc, header);
        }
    }

    private String getMonthFromHeader(Elements header) {
        String month = StringUtils.substringBetween(header.text(),",", ".");
        if (month.contains("-")) {
            month = StringUtils.substringAfter(month,"-");
        }
        return month;
    };

    private void initializeIndices() {
        Elements headerRow = table.select("td:contains(Commodity)").first().parent().select("td");
        for (int i = 0; i < headerRow.size(); i++) {
            String cellText = headerRow.get(i).text();
            if (cellText.contains("Commodity")) {
                this.commodityIndex = i;
            } else if (cellText.contains("Quantity Unit")) {
                this.unitIndex = i;
            } else if (cellText.equals(String.valueOf(month))) {
                this.quantityIndex = i;
                this.valueIndex = i + 1;
            }
        }
    }

    public List<Map<String, String>> extractData() {
        List<Map<String,String>> data = new ArrayList<>();
        for (String commodity: requiredCommodities) {
            Map<String, String> extractedData = new HashMap<>();
            String selector = String.format("td:contains(%s)", commodity);
            Elements row = table.select(selector).first().parent().select("td");
            extractedData.put("commodity", row.get(commodityIndex).text());
            extractedData.put("unit", row.get(unitIndex).text());
            extractedData.put("quantity", row.get(quantityIndex).text());
            extractedData.put("value", row.get(valueIndex).text());
            extractedData.put("month", month);
            data.add(extractedData);
        }
        return data;
    }

}

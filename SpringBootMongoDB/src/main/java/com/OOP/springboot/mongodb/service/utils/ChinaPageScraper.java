package com.OOP.springboot.mongodb.service.utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ChinaPageScraper {
    private final String[] requiredCommodities;
    private final String month;
    private final String year;
    private final Elements table;
    private final String url;
    private final String type;
//    private int unitIndex;
//    private int quantityIndex;
//    private int valueIndex;

    public ChinaPageScraper(String url, Document doc, Element header, String[] requiredCommodities, String type) {
        this.url = url;
        this.month = getMonthFromHeader(header);
        this.year = getYearFromHeader(header);
        this.table = doc.select("table");
        this.requiredCommodities = requiredCommodities;
        this.type = type;
//        initializeIndices();
    }

    public static ChinaPageScraper getInstance(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element header = doc.getElementsContainingOwnText("Commodities in Quantity and Value,").first();
        String headerText = header.text();
        if (headerText.contains("Import")) {
            return new ChinaImportsPageScraper(url, doc, header);
        } else {
            return new ChinaExportsPageScraper(url, doc, header);
        }
    }

    private String getMonthFromHeader(Element header) {
        String month = StringUtils.substringBetween(header.text(),",", ".");
        if (month.contains("-")) {
            month = StringUtils.substringAfter(month,"-");
        }
        return month;
    }

    private String getYearFromHeader(Element header) {
        return StringUtils.substringAfter(header.text(),".");
    }

//    private void initializeIndices() {
//        Elements headerRow = table.select("td:contains(Commodity)").first().parent().select("td");
//        for (int i = 0; i < headerRow.size(); i++) {
//            String cellText = headerRow.get(i).text();
//            if (cellText.contains("Quantity Unit")) {
//                this.unitIndex = i;
//            } else if (cellText.equals(month)) {
//                this.quantityIndex = i;
//                this.valueIndex = i + 1;
//            }
//        }
//    }

    public List<Map<String, String>> extractData() {
        List<Map<String,String>> data = new ArrayList<>();
        for (String commodity: requiredCommodities) {
            Map<String, String> extractedData = new HashMap<>();
            extractedData.put("commodity", commodity);
            extractedData.put("month", month);
            extractedData.put("year", year);
            extractedData.put("link", url);
            extractedData.put("type", type);
            String selector = String.format("td:contains(%s)", commodity);
            try {
                Elements row = table.select(selector).first().parent().select("td");
                extractedData.put("unit", row.get(1).text());
                extractedData.put("quantity", row.get(2).text());
                extractedData.put("value", row.get(3).text());
                extractedData.put("percent_change_quantity", row.get(row.size()-2).text());
                extractedData.put("percent_change_value", row.get(row.size()-1).text());
//                extractedData.put("unit", row.get(unitIndex).text());
//                extractedData.put("quantity", row.get(quantityIndex).text());
//                extractedData.put("value", row.get(valueIndex).text());
            } catch (RuntimeException e) {
                extractedData.put("error", "Commodity Not Found");
            }
            data.add(extractedData);
        }
        return data;
    }
}

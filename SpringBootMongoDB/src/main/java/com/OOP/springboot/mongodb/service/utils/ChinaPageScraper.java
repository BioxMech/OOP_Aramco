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

    public ChinaPageScraper(String url, Document doc, Element header, String[] requiredCommodities, String type) {
        this.url = url;
        this.month = getMonthFromHeader(header);
        this.year = getYearFromHeader(header);
        this.table = doc.select("table");
        this.requiredCommodities = requiredCommodities;
        this.type = type;
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
        return month.trim();
    }

    private String getYearFromHeader(Element header) {
        return StringUtils.substringAfter(header.text(),".").trim();
    }

    public List<Map<String, String>> extractData() {
        List<Map<String,String>> data = new ArrayList<>();
        UnitConverter unitConverter = new UnitConverter();
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
                String qty = unitConverter.convertToKbd(row.get(2).text(), 10000, "T", commodity, year, month);
                extractedData.put("quantity", qty);
                extractedData.put("value", row.get(3).text());
                extractedData.put("percent_change_quantity", row.get(row.size()-2).text());
                extractedData.put("percent_change_value", row.get(row.size()-1).text());
            } catch (RuntimeException e) {
                extractedData.put("error", "Commodity Not Found");
            }
            data.add(extractedData);
        }
        return data;
    }
}

package com.OOP.springboot.mongodb.service.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChinaLinkScraper {
    private static final String rootUrl = "http://english.customs.gov.cn/statics/report/monthly";
    private final String[] requiredTitles= {
        "Major Export Commodities in Quantity and Value",
        "Major Import Commodities in Quantity and Value"
    };

    private List<String> getLinks(String url) throws IOException {
        List<String> scrapedLinks = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        for (String title: requiredTitles) {
            Element row = doc.select(String.format("td:contains(%s)", title)).first().parent();
            Elements links = row.select("a");
            for (Element link: links) {
                scrapedLinks.add(link.attr("abs:href"));
            }
        }
        return scrapedLinks;
    }

    public List<String> scrapeAll() throws IOException {
        List<String> extractedLinks = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2018; i < currentYear ;i++) {
            String url = rootUrl + i + ".html";
            extractedLinks.addAll(getLinks(url));
        }
        extractedLinks.addAll(getLinks(rootUrl + ".html"));
        return extractedLinks;
    }

//    public String[] scrapeLatest() {
//        return ;
//    }
}

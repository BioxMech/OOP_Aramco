package com.OOP.springboot.mongodb.service.utils;

import com.OOP.springboot.mongodb.service.ChinaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class ChinaLinkScraper {
    private static final String rootUrl = "http://english.customs.gov.cn/statics/report/monthly";
    private final String[] requiredTitles= {
        "Major Export Commodities in Quantity and Value",
        "Major Import Commodities in Quantity and Value"
    };

    @Autowired
    private ChinaService chinaService;

    private List<String> getLinks(int month, String url) throws IOException {
        List<String> scrapedLinks = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        for (String title: requiredTitles) {
            Element row = doc.select(String.format("td:contains(%s)", title)).first().parent();
            Elements links = row.select("a");
            for (int m = month; m < links.size(); m++) {
                scrapedLinks.add(links.get(m).attr("abs:href"));
            }
        }
        return scrapedLinks;
    }

    public List<String> scrapeMissing() throws IOException {
        List<String> dbLatestYearMonth = chinaService.getLatestYearMonth();
        int dbLatestYear = 2018;
        int dbLatestMonth = 1;
        if (dbLatestYearMonth != null) {
            dbLatestYear = Integer.parseInt(dbLatestYearMonth.get(0));
            dbLatestMonth = Integer.parseInt(dbLatestYearMonth.get(1));
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> extractedLinks = new ArrayList<>();
        for (int year = dbLatestYear + 1; year < currentYear; year++) {
            String url = rootUrl + year + ".html";
            extractedLinks.addAll(getLinks(0, url));
        }
        if (dbLatestYear == currentYear) {
            extractedLinks.addAll(getLinks(dbLatestMonth, rootUrl + ".html"));
        } else {
            extractedLinks.addAll(getLinks(0, rootUrl + ".html"));
            extractedLinks.addAll(getLinks(dbLatestMonth, rootUrl + dbLatestYear + ".html"));
        }
        return extractedLinks;
    }
}

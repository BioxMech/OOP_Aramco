package com.OOP.springboot.mongodb.service.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ChinaExportsPageScraper extends ChinaPageScraper{
    private static final String[] requiredCommodities = {
            "Gasoline",
            "Aviation kerosene",
            "Diesel oil"
    };

    public ChinaExportsPageScraper(String url, Document doc, Element header) {
        super(url, doc, header, requiredCommodities, "export");
    }
}

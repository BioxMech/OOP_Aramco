package com.OOP.springboot.mongodb.service.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ChinaImportsPageScraper extends ChinaPageScraper{
    private static final String[] requiredCommodities = {
            "Crude petroleum oil",
            "Naphtha",
            "Kerosene",
            "Natural gases"
    };

    public ChinaImportsPageScraper(String url, Document doc, Element header) {
        super(url, doc, header, requiredCommodities, "import");
    }

}

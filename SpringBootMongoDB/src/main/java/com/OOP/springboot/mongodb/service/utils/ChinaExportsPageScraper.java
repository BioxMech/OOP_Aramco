package com.OOP.springboot.mongodb.service.utils;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ChinaExportsPageScraper extends ChinaPageScraper{
    private static final String[] requiredCommodities = {
            "Gasoline",
            "Aviation kerosene",
            "Diesel oil"
    };

    public ChinaExportsPageScraper(Document doc, Elements header) {
        super(doc, header, requiredCommodities);
    }
}

package com.OOP.springboot.mongodb.service.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ChinaImportsPageScraper extends ChinaPageScraper{
    private static final String[] requiredCommodities = {
            "Crude petroleum oils",
            "Naphtha",
            "Aviation kerosene",
            "Natural gases"
    };

    public ChinaImportsPageScraper(Document doc, Elements header) {
        super(doc, header, requiredCommodities);
    }

}

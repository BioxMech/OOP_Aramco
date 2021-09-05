package com.OOP.springboot.mongodb.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CrawlerService {

    private List<String> links;

    public CrawlerService(List<String> links) {
        this.links = new ArrayList<>();
    }

    @GetMapping("/website")
    public List<String> getPageLinks(String URL) {

        //4. Check if you have already crawled the URLs
        //(we are intentionally not checking for duplicate content in this example)
        if (!links.contains(URL)) {
            try {
                //4. (i) If not add it to the index
//                if (links.add(URL)) {
//                    System.out.println(URL);
//                }

                //2. Fetch the HTML code
                Document document = Jsoup.connect(URL).get();
                //3. Parse the HTML to extract links to other URLs
                Elements newsHeadlines = document.select("#mp-itn b a");

                String link;
                //5. For each extracted URL... go back to Step 4.
                for (Element headline : newsHeadlines) {

                    link = headline.attr("title") + headline.absUrl("href");
                    links.add(link);
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
        return links;
    }
}

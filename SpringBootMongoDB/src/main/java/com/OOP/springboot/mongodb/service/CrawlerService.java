package com.OOP.springboot.mongodb.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class CrawlerService {

    private final List<String> links;
    private final List<String> filesToScrape = new ArrayList<>(Arrays.asList(
            "Table 2.1-1: Production of Crude Oil",
            "Table 2.1-2: Production of Condensate",
            "Table 2.1-4: Quantity and Value of Petroleum Products Import",
            "Table 2.1-5: Quantity and Value of Petroleum Products Export",
            "Table 2.2-2: Material Intake",
            "Table 2.3-2: Production of Petroleum Products (Barrel/Day)"
    ));
    public CrawlerService(List<String> links) {
        this.links = new ArrayList<>();
    }

    @GetMapping("/website")
    public List<String> getPageLinks(String URL) {

        try {
            // Fetch the HTML code
            Document document = Jsoup.connect(URL).get();
            // Parse the HTML to extract links to other URLs
            Elements elementData = document.select(".catItemBody");

            // Store a local variable of the link when looping (for better debugging purpose too)
            String link;

            for (Element e : elementData) {
                // Go to the 1st div element and look at its text (in terms of js, value)
                String rowName = e.firstElementSibling().selectFirst("div").text();
                // Check if it is the right row we are looking for
                if (filesToScrape.contains(rowName)) {
                    // Obtain the link
                    link = e.selectFirst("a").absUrl("href");
                    // Returning purpose
                    links.add(link);

                    // To obtain the raw bytes of the excel file from the link
                    byte[] bytes = Jsoup.connect(link)
                        .header("Accept-Encoding", "xls")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                            .referrer("http://www.eppo.go.th/index.php/en/en-energystatistics/petroleum-statistic")
                            .ignoreContentType(true)
                            .maxBodySize(0)
                            .timeout(600000)
                            .execute()
                            .bodyAsBytes();
                    try {
                        // Name of the of the file - taken from the website
                        String savedFileName = rowName.substring(13);

                        if (savedFileName.contains("/")) {
                            savedFileName = savedFileName.replace("/", " per ");
                        }
                        savedFileName = savedFileName.concat(".xls");
//                            if (!savedFileName.endsWith(".xls")) savedFileName.concat(".xls");

                        // To create the file (set in the excel_files folder)
                        FileOutputStream fos = new FileOutputStream("./excel_files/" + savedFileName);
                        fos.write(bytes);
                        fos.close();


                        System.out.println(savedFileName + " has been downloaded.");

                        // TODO: To complete the excel reading

                        // For deleting the files after reading it
//                        File f = new File("./excel_files/" + savedFileName);
//                        if (f.delete()) {
//                            System.out.println("Successful");
//                        }

                    } catch (IOException err) { // if file/link failed to be found, it will throw a checked error
                        System.err.println("Could not read the file at '" + link);
                        System.err.println("System error message: " + err.getMessage());
                    }
                }
            }
        } catch (IOException e) { // Same as the above - if URL cannot be found
            System.err.println("For '" + URL + "': " + e.getMessage());
        }

        return links;
    }
}

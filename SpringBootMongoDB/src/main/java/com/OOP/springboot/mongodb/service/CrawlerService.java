package com.OOP.springboot.mongodb.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class CrawlerService {

    private final List<String> links;
    private final HashMap<String, String> thailandLinks = new HashMap<>();
    private final List<String> thailandDataRequiredTitle = new ArrayList<>(Arrays.asList(
            "Table 2.1-1: Production of Crude Oil"
//            "Table 2.1-2: Production of Condensate",
//            "Table 2.1-4: Quantity and Value of Petroleum Products Import",
//            "Table 2.1-5: Quantity and Value of Petroleum Products Export",
//            "Table 2.2-2: Material Intake",
//            "Table 2.3-2: Production of Petroleum Products (Barrel/Day)"
    ));
    private final List<String> chinaDataRequiredTitle = new ArrayList<>(Arrays.asList(
            "（13）Major Export Commodities in Quantity and Value",
            "（14）Major Import Commodities in Quantity and Value"
    ));
    public CrawlerService(List<String> links) {
        this.links = new ArrayList<>();
    }

    // Thailand Web Scraping Service
    public List<Map<String, List<Map<String, Map<String, Integer>>>>> scrapeThailand() {
        String URL = "http://www.eppo.go.th/index.php/en/en-energystatistics/petroleum-statistic";
        // Initialize list
        List<Map<String, List<Map<String, Map<String, Integer>>>>> dataObjects = new ArrayList<>();

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
                if (thailandDataRequiredTitle.contains(rowName)) {
                    // Obtain the link
                    link = e.getElementsByIndexEquals(2).select("a").attr("abs:href");
                    thailandLinks.put(rowName, link);
                    links.add(link);

                }
            }
            for (Map.Entry<String, String> entry : thailandLinks.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value.contains("T02_01_01")) {
                    ThailandCrudeOilProductionScraper excelScraper = new ThailandCrudeOilProductionScraper(value, key);
                    dataObjects = excelScraper.scrapeThailand();
                }
            }
        } catch (IOException e) { // Same as the above - if URL cannot be found
            System.err.println("For '" + URL + "': " + e.getMessage());
        }

        System.out.println(dataObjects);
        return dataObjects;
    }


//    public HashMap<Integer, HashMap<String, HashMap <String, ArrayList<Double>>>> scrapeThailand(String URL) {
//
//        try {
//            // Fetch the HTML code
//            Document document = Jsoup.connect(URL).get();
//            // Parse the HTML to extract links to other URLs
//            Elements elementData = document.select(".catItemBody");
//
//            // Store a local variable of the link when looping (for better debugging purpose too)
//            String link;
//
//            for (Element e : elementData) {
//                // Go to the 1st div element and look at its text (in terms of js, value)
//                String rowName = e.firstElementSibling().selectFirst("div").text();
//                // Check if it is the right row we are looking for
//                if (thailandDataRequiredTitle.contains(rowName)) {
//                    // Obtain the link
////                    link = e.selectFirst("a").absUrl("href");
//                    link = e.getElementsByIndexEquals(2).select("a").attr("abs:href");
//
//                    // Returning purpose
//                    links.add(link);
//
//                    // To obtain the raw bytes of the excel file from the link
//                    byte[] bytes = Jsoup.connect(link)
//                        .header("Accept-Encoding", "xls")
//                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
//                            .referrer("http://www.eppo.go.th/index.php/en/en-energystatistics/petroleum-statistic")
//                            .ignoreContentType(true)
//                            .maxBodySize(0)
//                            .timeout(600000)
//                            .execute()
//                            .bodyAsBytes();
//                    try {
//                        // Name of the of the file - taken from the website
//                        String savedFileName = rowName.substring(13);
//
//                        if (savedFileName.contains("/")) {
//                            savedFileName = savedFileName.replace("/", " per ");
//                        }
//                        savedFileName = savedFileName.concat(".xls");
////                            if (!savedFileName.endsWith(".xls")) savedFileName.concat(".xls");
//
//                        // To create the file (set in the excel_files folder)
//                        FileOutputStream fos = new FileOutputStream("./excel_files/" + savedFileName);
//                        fos.write(bytes);
//                        fos.close();
//
//                        System.out.println(savedFileName + " has been downloaded.");
//
//                        // TODO: To complete the excel reading
//                        double current_year = Calendar.getInstance().get(Calendar.YEAR);
//                        FileInputStream excel_file = new FileInputStream("./excel_files/" + savedFileName);
//                        Workbook wb = new HSSFWorkbook(excel_file);
//                        Sheet sheet = wb.getSheetAt(0);
//                        Iterator<Row> rows = sheet.iterator();
//
//                        //String array to see what rows to read
//                        String[] rowsToRead = {"CRUDE OIL", "PETROLEUM PRODUCTS", "","BBL/D", "PRICE"};
//
//                        //Apache POI Data Formatter to get Cell Values
//                        DataFormatter formatter = new DataFormatter();
////                        formatter.formatCellValue()
//
//                        int currentYear = 0;
//                        int rowCount = sheet.getPhysicalNumberOfRows();
//
//                        //How many chunks of year-data
//                        int yearsToLoop = rowCount/29;
//                        System.out.println("Years to be Looped: " + yearsToLoop);
//                        int latestChunk = rowCount-28;
//
//                        //Pre-create hashmap for data store
//                        HashMap<Integer, HashMap<String, HashMap <String, ArrayList<Double>>>> importQtyValPetrolProducts= new HashMap<>();
////                         importQtyValPetrolProducts : { Year : { Month : { Title : { Property1: Value}, ... {Property3 : Value}}}}
//                        System.out.println("No. of Rows: " + rowCount);
//
//                        // Original numeric is in double so we cast it to int
//                        currentYear = (int) sheet.getRow(latestChunk).getCell(0).getNumericCellValue();
////                        Object yearCellValue = formatter.formatCellValue(sheet.getRow(latestChunk).getCell(0));
//                        System.out.println("Current Year: " + currentYear);
//
//                        List<Integer> importantRows = Arrays.asList(5, 6, 7, 12, 13, 14, 19, 20, 21, 26, 27, 28);
//                        List<Integer> titleRows = Arrays.asList(2, 9, 16, 23);
//
//                        //Create a hashmap in preparation for property:value rows
//                        HashMap<String, HashMap <String, ArrayList<Double>>> productData = new HashMap<>();
//                        String titleToSave = "";
//                        ///////////////////////////////////////////////////
//                        ///// SWITCH FOR LOOP WHEN TESTING vs ACTUAL  /////
//                        ///////////////////////////////////////////////////
////                        for (int yearLoopCount=1 ; yearLoopCount < 3; yearLoopCount++) {
//                        for (int yearLoopCount = 1; yearLoopCount < yearsToLoop+1; yearLoopCount++) {
//                            // Reading all the data for the year (28 rows of data)
//                            if (yearLoopCount == 1) {
//                                latestChunk = rowCount - 28;
//                            }
//                            else{
//                                latestChunk = rowCount - (yearLoopCount * 29) + 1;
//
//                                System.out.println("Cell Type for Title Row: " + (sheet.getRow(latestChunk).getCell(0).getCellType()));
//                                System.out.println("Current CHUNK IS AT " + (latestChunk + 1));
//
//                                // Original numeric is in double so we cast it to int
//                                currentYear = (int) sheet.getRow(latestChunk).getCell(0).getNumericCellValue();
////                                System.out.println(currentYear);
//                            }
//
//                            for (int i=2; i < 29; i++) {
//                                if (titleRows.contains(i)) {
//                                    //Create a new HashMap when there's a new title row
//                                    titleToSave = sheet.getRow(latestChunk+i).getCell(0).getStringCellValue();
//                                }
//
//                                if (importantRows.contains(i)) {
//                                    //Title Column
//                                    String rowTitle = sheet.getRow(latestChunk+i).getCell(0).getStringCellValue();
////                                System.out.println("Row title: " + i + " " + rowTitle);
//                                    HashMap<String, ArrayList<Double>> propertyValue = new HashMap<>();
//                                    ArrayList<Double> rowValues = new ArrayList<>();
//
//                                    for ( int colToRead=1; colToRead<12; colToRead++) {
//                                        rowValues.add(sheet.getRow(latestChunk+i).getCell(colToRead).getNumericCellValue());
//                                    }
//                                    //Push into the hashmap for the property and list of values
//                                    propertyValue.put(rowTitle, rowValues);
//                                    productData.put(titleToSave, propertyValue);
//                                    System.out.print(currentYear); //Key to be pushed into final HashMap
//                                    System.out.println(productData); //Value to be pushed into final HashMap
//                                    importQtyValPetrolProducts.put(currentYear,productData);
////                                System.out.println(propertyValue);
//                                }
//                            }
//                        }
//                        System.out.println("FINAL HASHMAP!");
//                        System.out.println(importQtyValPetrolProducts);
//
//
//                            //Read all the columns in each row
////                            HashMap<>
////                            for (int colNum=1; colNum < 12; colNum++) {
//
////                                String colValue = sheet.getRow(latestChunk+i).getCell(colNum).getStringCellValue();
////                            }
//
//
//                        //Outer:
////                        while (rows.hasNext()) {
////                            Row currentRow = rows.next();
////
////                            if (!current_year_section && currentRow.getCell(dateColIndex) != null) {
////                                if (currentRow.getCell(dateColIndex).getCellType()    == CellType.STRING &&
////                                        currentRow.getCell(dateColIndex).getStringCellValue().trim().equals("2021")) {
////                                    current_year_section = true;
////                                }
////                                else if (currentRow.getCell(dateColIndex).getCellType() == CellType.NUMERIC &&
////                                        currentRow.getCell(dateColIndex).getNumericCellValue() == current_year) {
////                                    current_year_section = true;
////                                }
////                            }
////
////                            if (!found) {
////                                Iterator<Cell> cellsInRow = currentRow.iterator();
////
////                                // To find the "Total" column index in the excel file
////                                while (cellsInRow.hasNext()) {
////                                    Cell currentCell = cellsInRow.next();
////                                    if (currentCell.getCellType() == CellType.STRING && currentCell.getStringCellValue().equals("Total")) {
////                                        totalColIndex = currentCell.getColumnIndex();
////                                        found = true;
////                                        continue Outer;
////                                    }
////                                }
////                            }
//
////                        int dateColIndex = 0;
////                        int totalColIndex = 0;
////                        boolean found = false;
////                        double ytd_total = 0;
////                        boolean current_year_section = false;
////
////                        Outer:
////                        while (rows.hasNext()) {
////                            Row currentRow = rows.next();
////
////                            if (!current_year_section && currentRow.getCell(dateColIndex) != null) {
////                                if (currentRow.getCell(dateColIndex).getCellType() == CellType.STRING &&
////                                        currentRow.getCell(dateColIndex).getStringCellValue().trim().equals("2021")) {
////                                    current_year_section = true;
////                                }
////                                else if (currentRow.getCell(dateColIndex).getCellType() == CellType.NUMERIC &&
////                                        currentRow.getCell(dateColIndex).getNumericCellValue() == current_year) {
////                                    current_year_section = true;
////                                }
////                            }
////
////                            if (!found) {
////                                Iterator<Cell> cellsInRow = currentRow.iterator();
////
////                                // To find the "Total" column index in the excel file
////                                while (cellsInRow.hasNext()) {
////                                    Cell currentCell = cellsInRow.next();
////                                    if (currentCell.getCellType() == CellType.STRING && currentCell.getStringCellValue().equals("Total")) {
////                                        totalColIndex = currentCell.getColumnIndex();
////                                        found = true;
////                                        continue Outer;
////                                    }
////                                }
////                            }
////                            // Checking if the current row is "YTD"
////                            else if (current_year_section && currentRow.getCell(dateColIndex).getCellType() == CellType.STRING &&
////                                    currentRow.getCell(dateColIndex).getStringCellValue().trim().equals("YTD")) {
////                                ytd_total = currentRow.getCell(totalColIndex).getNumericCellValue();
////                                links.add(ytd_total + "");
////                            }
////                        }
//
//                        // Close the workbook and stream
//                        wb.close();
//                        excel_file.close();
//
//                        // Delete the files after reading it
//                        File f = new File("./excel_files/" + savedFileName);
//                        if (f.delete()) {
//                            System.out.println("Successful");
//                        }
//
//                    } catch (IOException err) { // if file/link failed to be found, it will throw a checked error
//                        System.err.println("Could not read the file at '" + link);
//                        System.err.println("System error message: " + err.getMessage());
//                    }
//                }
//            }
//        } catch (IOException e) { // Same as the above - if URL cannot be found
//            System.err.println("For '" + URL + "': " + e.getMessage());
//        }
//        return links;
//        return importQtyValPetrolProducts;
//    }


    // TODO: China Web scraping service
    public List<Map<String, String>> scrapeChina(String URL) {

        // TODO Retrieve and store XLS file

        // Initialize list
        List<Map<String, String>> dataObjects = new ArrayList<>();

        try {
            // Fetch the HTML code
            Document document = Jsoup.connect(URL).get();
            // Parse the HTML to extract links to other URLs
            Elements elementData = document.select("table tr td"); // table row tag

            // Store a local variable of the link when looping (for better debugging purpose too)
            String link;

            // Checker to see if the next td is part of the required data
            boolean isRequired = false;

            // Looping through the elements that you have extracted in elementData
            for (Element e : elementData) {
                String title = e.text().trim();
//                System.out.println(title);
                if (chinaDataRequiredTitle.contains(title)) {
                    isRequired = true;
                    continue;
                }

                // TODO Extract links for all the months
                if (isRequired) {
                    // find the anchor tags > pick the last one > get the absolute URL
                    link = e.select("a").last().absUrl("href"); // retrieve the link of the latest available month
                    links.add(link); // add it into the links arrayList
                    isRequired = false; // reset the boolean value
                }
            }


            // ArrayList containing the elements that have to be retrieved from both pages
            ArrayList<String> importData = new ArrayList<>(Arrays.asList(
                    "Crude petroleum oils",
                    "Naphtha",
                    "Aviation kerosene",
                    "Natural gases"
            ));
//            ArrayList<String> exportData = new ArrayList<>();

            // Retrieve data from the 2 links that we have scraped from the main page
            for (String dataLink: links){
                System.out.println(dataLink);

                // Fetch the HTML code
                Document dataDocument = Jsoup.connect(dataLink).get();

//                System.out.println(dataDocument.outerHtml());

                // Parse the HTML to extract data required
                Elements header = dataDocument.select("div.atcl-ttl");


                // Splitting import and export data
                if (header.text().contains("Import")) {
                    System.out.println("Import Logic");

                    // Extract row data
                    Elements importRowData = dataDocument.select("table.ke-zeroborder tr");
                    for (Element row: importRowData){

                        if (importData.contains(row.select("td:first-child").text())){

                            System.out.println(row.select("td:first-child").text());

                            // Assign title
                            String title = row.select("td:first-child").text();

                            //                                JSONObject crudePetroleumOil = new JSONObject();
                            Map<String, String> chinaDataObj = new HashMap<>();
                            chinaDataObj.put("type", title);

                            // TODO To insert month here

                            chinaDataObj.put("quantity_unit", row.getElementsByTag("td").get(1).text());
                            chinaDataObj.put("month_quantity", row.getElementsByTag("td").get(2).text());
                            chinaDataObj.put("month_value", row.getElementsByTag("td").get(3).text());
                            chinaDataObj.put("jan_to_month_quantity", row.getElementsByTag("td").get(4).text());
                            chinaDataObj.put("jan_to_month_value", row.getElementsByTag("td").get(5).text());
                            chinaDataObj.put("percentage_change_quantity", row.getElementsByTag("td").get(6).text());
                            chinaDataObj.put("percentage_change_value", row.getElementsByTag("td").get(7).text());


//                                System.out.println(chinaDataObj);

                            // Add to dataObjects
                            dataObjects.add(chinaDataObj);
                        }
                    }
                }
                else {
                    System.out.println("Export Logic");
                    // TODO Export Logic
                }




            }

        } catch (IOException e) { // Same as the above - if URL cannot be found
            System.err.println("For '" + URL + "': " + e.getMessage());
        }

        System.out.println(dataObjects);
        return dataObjects;
    }
}

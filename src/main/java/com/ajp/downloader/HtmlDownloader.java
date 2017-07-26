/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ajp.downloader;

import com.ajp.models.SearchResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Loh Jing Yen
 */
public class HtmlDownloader implements Runnable {

    private final String url;
    private final String input;
    private final String downloadPath;
    private final SearchResult searchResult;
    private final Map<String, File> urlToFile;
//    private final Map<String, SearchResult> URI_TO_RESULT = new HashMap<>();

    public HtmlDownloader(String url,
            String input,
            String downloadPath,
            SearchResult searchResult,
            Map<String, File> urlToFile) {
        this.url = url;
        this.input = input;
        this.downloadPath = downloadPath;
        this.searchResult = searchResult;
        this.urlToFile = urlToFile;
    }

    @Override
    public void run() {

        String uuid = UUID.randomUUID().toString();

        try {
            File file = new File(downloadPath + "/" + uuid + ".html");

            if (file.exists()) {
                file.delete();
            }

            if (file.createNewFile()) {

                System.out.println(file.getAbsolutePath());

                try (PrintWriter pr = new PrintWriter(file)) {
                    urlToFile.put(url, file);
//                    URI_TO_RESULT.put(uri, searchResult);

                    Document doc = Jsoup.connect(url).get();
                    String docStr = doc.html();

                    Pattern inputPattern = Pattern.compile(input, Pattern.CASE_INSENSITIVE);
                    Matcher inputMatcher = inputPattern.matcher(docStr);

                    // Get the no of times the search query appears in the seed
                    int count = 0;
                    while (inputMatcher.find()) {
                        count++;
                    }
                    searchResult.setOccurance(count);

                    pr.print(doc.toString());
                    System.out.println("File written");
                } catch (IOException ex) {
                    Logger.getLogger(HtmlDownloader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HtmlDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

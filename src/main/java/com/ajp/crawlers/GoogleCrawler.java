/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ajp.crawlers;

import com.ajp.models.SearchResult;
import com.ajp.models.SearchResult.SearchEngine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
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
public final class GoogleCrawler extends SearchEngineCrawler {

    private static final String BASE_URL = "https://www.google.com.sg/search?q=";

    // <editor-fold desc="Some regex constants" defaultstate="collapsed">
    private static final String TITLE_REGEX = "<h3 class=\"r\"><a href=\"(.*?)\">(.*?)<\\/a><\\/h3>";
    private static final Pattern TITLE_PATTERN = Pattern.compile(TITLE_REGEX, Pattern.DOTALL);

    private static final String LINK_REGEX = "<a href=\"(.*?)\"";
    private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.DOTALL);

    private static final String DESCRIPTION_REGEX = "<span class=\"st\">(.*?)<\\/span>";
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(DESCRIPTION_REGEX, Pattern.DOTALL);

    private static final String RESULT_REGEX = "<div class=\"rc\">(.*?)<!--n-->";
    private static final Pattern RESULT_PATTERN = Pattern.compile(RESULT_REGEX, Pattern.DOTALL);
    // </editor-fold>

    private final String input;
    private final List<SearchResult> searchResults;
    private final BlockingQueue<SearchResult> queue;

    public GoogleCrawler(String input, BlockingQueue<SearchResult> queue) {
        this.input = input;
        this.queue = queue;
        searchResults = new ArrayList<>();
    }

    @Override
    public void run() {

        try {
            String query = input.replace(" ", "+");
            Document doc = Jsoup.connect(BASE_URL + query).get();

            Matcher m = RESULT_PATTERN.matcher(doc.toString());

            if (m.find()) {
                do {
                    String match = m.group(1);
                    System.out.println("Google match found.");
                    SearchResult searchResult = new SearchResult();
                    searchResult.setSearchedTerm(input);

                    Matcher matcher = LINK_PATTERN.matcher(match);
                    if (matcher.find()) {
                        String url = matcher.group(1);
                        System.out.println(url);
                        searchResult.setUrl(url);
                        searchResult.setHtml(Jsoup.connect(url).get().toString());
                    } else {
                        System.out.println("No url found");
                    }

                    matcher = TITLE_PATTERN.matcher(match);
                    if (matcher.find()) {
                        String title = matcher.group(1)
                                .replaceAll("<.*?>", "")
                                .replaceAll("&nbsp;", " ");
                        System.out.println(title);
                        searchResult.setTitle(title);
                    } else {
                        System.out.println("No title found");
                    }

                    matcher = DESCRIPTION_PATTERN.matcher(match);
                    if (matcher.find()) {
                        String description = matcher.group(1)
                                .replaceAll("<.*?>", "")
                                .replaceAll("&nbsp;", "");
                        System.out.println(description);
                        searchResult.setDescription(description);
                    } else {
                        System.out.println("No description found");
                    }

                    searchResult.getSearchedBy().add(SearchEngine.GOOGLE);

                    searchResults.add(searchResult);

                    if (queue.remainingCapacity() == 0) {
                        System.out.println("queue full");
                        break;
                    }

                    if (queue.contains(searchResult)) {
                        // logic to append to searchedBy
                        SearchResult res = queue.stream()
                                .filter(sr -> sr.equals(searchResult))
                                .findFirst()
                                .get();
                        res.getSearchedBy().add(SearchEngine.GOOGLE);
                        System.out.println("Duplicate result, adding Google to to its searchedBy list.");
                    } else {
                        System.out.println("New result added.");
                        queue.add(searchResult);
                    }

                } while (m.find(m.start(1)));
            } else {
                System.out.println("No Google result found at all!");
            }

        } catch (IOException ex) {
            Logger.getLogger(GoogleCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Google done!");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getQuery() {
        return input;
    }

    public List<SearchResult> getResults() {
        return searchResults;
    }

}

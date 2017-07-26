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
    private static final String TITLE_REGEX = "<h3.*?><a.*?>(.*?)</a></h3>";
    private static final Pattern TITLE_PATTERN = Pattern.compile(TITLE_REGEX, Pattern.DOTALL);

    private static final String LINK_REGEX = "<h3.*?><a href=\"([^\\\\/].*?)\"";
    private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.DOTALL);

    private static final String DESCRIPTION_REGEX = "<span class=\"st\">(.*?)</span>";
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(DESCRIPTION_REGEX, Pattern.DOTALL);

    private static final String RESULT_REGEX = "<div\\s?class=\"g\"><h3\\s?class=\"r\">(.*?)<br><\\/div>";
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

    // <editor-fold desc="Old code" defaultstate="collapsed">
//    private final List<String> seeds;
//    private List<SearchResult> searchResults;
//
//    public GoogleCrawler() {
//        seeds = new ArrayList<>();
//        searchResults = null;
//    }
//
//    @Override
//    public List<SearchResult> getSearchResults(String query) {
//        List<SearchResult> results = this.getSeeds(query)
//                .getSeedsLinks()
//                .setSeedsTitles()
//                .setSeedsDescriptions()
//                .addSearchedByEngine();
//        return results;
//    }
//    
//    public List<SearchResult> addSearchedByEngine() {
//        searchResults.forEach(result -> result.getSearchedBy().add("google"));
//        return searchResults;
//    }
//
//    @Override
//    protected GoogleCrawler getSeeds(String query) {
//
//        final String uri = BASE_URL + query + "&num=2";
//
//        Document doc = new Document(uri);
//        try {
//            doc = Jsoup.connect(uri).get();
//        } catch (IOException ex) {
//            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        String docBody = doc.body().toString();
//
//        Pattern p = Pattern.compile(RESULT_PATTERN, Pattern.DOTALL);
//        Matcher m = p.matcher(docBody);
//        if (m.find()) {
//            do {
//                seeds.add(m.group(1));
//                System.out.println("Google seed: " + m.group(1));
//            } while (m.find(m.start(1)));
//        }
//        return this;
//    }
//
//    @Override
//    protected GoogleCrawler getSeedsLinks() {
//
//        Pattern p = Pattern.compile(LINK_PATTERN, Pattern.DOTALL);
//
//        searchResults = seeds.stream()
//                .map(p::matcher)
//                .filter(Matcher::find)
//                .map(linkMatcher -> {
//                    SearchResult srm = new SearchResult();
//                    try {
//                        srm.setUrl(new URL(linkMatcher.group(1)));
//                    } catch (MalformedURLException e) {
//                        srm.setUrl(null);
//                    }
//                    return srm;
//                })
//                .collect(Collectors.toList());
//        return this;
//    }
//
//    public GoogleCrawler setSeedsTitles() {
//
//        Pattern p = Pattern.compile(TITLE_PATTERN, Pattern.DOTALL);
//
//        for (int i = 0; i < seeds.size(); i++) {
//            Matcher m = p.matcher(seeds.get(i));
//            if (m.find()) {
//
//                String title = m.group(1);
//                title = title.replaceAll("<.*?>", "");
//                searchResults.get(i).setTitle(title);
//            }
//        }
//        return this;
//    }
//
//    public GoogleCrawler setSeedsDescriptions() {
//
//        Pattern p = Pattern.compile(DESCRIPTION_PATTERN, Pattern.DOTALL);
//
//        for (int i = 0; i < seeds.size(); i++) {
//            Matcher m = p.matcher(seeds.get(i));
//            if (m.find()) {
//                String description = m.group(1).replaceAll("<.*?>", "");
//                searchResults.get(i).setDescription(description);
//            }
//        }
//        return this;
//    }
    // </editor-fold>
    @Override
    public void run() {

        try {
            String query = input.replace(" ", "+");
            Document doc = Jsoup.connect(BASE_URL + query)
                    .userAgent("Googlebot/2.1").get();

            Matcher m = RESULT_PATTERN.matcher(doc.toString());

            if (m.find()) {
                do {
                    String match = m.group(1);
                    System.out.println("Google match found: " + match);
                    SearchResult searchResult = new SearchResult();
                    searchResult.setSearchedTerm(input);
//                    results.add(match);
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
                        String description = matcher.group(1).replaceAll("<.*?>", "");
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
                        System.err.println("Queue is full but idk how to append yet");
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ajp.searcher;

import com.ajp.crawlers.BingCrawler;
import com.ajp.crawlers.GoogleCrawler;
import com.ajp.downloader.HtmlDownloader;
import com.ajp.models.SearchResult;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Loh Jing Yen
 */
public class SearchEngineSearcher {

    private final Map<String, File> urlToFile;
    private final BlockingQueue<SearchResult> links;

    public SearchEngineSearcher(Map<String, File> urlToFile,
            BlockingQueue<SearchResult> links) {
        this.urlToFile = urlToFile;
        this.links = links;
    }

    public void search(String query, int noOfThreads, String path) {

        GoogleCrawler googleCrawler = new GoogleCrawler(query, links);
        BingCrawler bingCrawler = new BingCrawler(query, links);

        List<Runnable> tasks = Arrays.asList(googleCrawler, bingCrawler);

        ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
		
        try {
            CompletableFuture[] cf = tasks.stream()
                    .map(task -> CompletableFuture.runAsync(task, executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(cf).join();

            System.out.println("Queue should be full");

            cf = links.stream()
                    .map(sr -> {
                        String url = sr.getUrl();
                        String searchedTerm = sr.getSearchedTerm();
                        HtmlDownloader downloader
                                = new HtmlDownloader(url, searchedTerm, path, sr, urlToFile);
                        return downloader;
                    })
                    .map(task -> CompletableFuture.runAsync(task, executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(cf).join();

            System.out.println("Files should be downloaded");

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
        }

        // <editor-fold desc="Old code" defaultstate="collapsed">
//        ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
//        try {
//
//            for (int i = 0; i < LINKS.size(); i++) {
//                SearchResult searchResult = LINKS.take();
//                HtmlDownloader htmlDownloader = new HtmlDownloader(
//                        searchResult.getUrl(),
//                        searchResult
//                );
//                executor.execute(htmlDownloader);
//            }
//
//            executor.shutdown();
//            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(SearchEngineSearcher.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (!executor.isTerminated()) {
//                System.err.println("cancel non-finished tasks");
//            }
//            executor.shutdownNow();
//            System.out.println("shutdown finished");
//        }
        // </editor-fold>
    }

}

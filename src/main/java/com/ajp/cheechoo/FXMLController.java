package com.ajp.cheechoo;

import com.ajp.models.SearchResult;
import com.ajp.searcher.SearchEngineSearcher;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FXMLController implements Initializable {

    //<editor-fold desc="FXML controls" defaultstate="collapsed">
    // General controls
    @FXML
    private Slider threadSlider;
    @FXML
    private Button searchButton;
    @FXML
    private Label searchBoxLabel;
    @FXML
    private Button changeDirButton;
    @FXML
    private WebView displayWebView;
    @FXML
    private TextField searchTxtField;
    @FXML
    private ListView<SearchResult> resultsListView;

    // Labels with changing text
    @FXML
    private Label pathLabel;
    @FXML
    private Label timeTakenLabel;
    @FXML
    private Label occurancesLabel;
    @FXML
    private Label descriptionLabel;

    //</editor-fold>
    private String downloadPath;

    private WebEngine webEngine;

    private List<SearchResult> seedList;
    private ObservableList<SearchResult> searchResults;

    private final Map<String, File> urlToFile = new HashMap<>();
    private final BlockingQueue<SearchResult> queue = new ArrayBlockingQueue<>(12);

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        webEngine = displayWebView.getEngine();

        seedList = new ArrayList<>();
        searchResults = FXCollections.observableList(seedList);
        resultsListView.setItems(searchResults);

        searchTxtField.setText("san andreas");
    }

    @FXML
    private void startCrawling(ActionEvent event) {

        searchResults.clear();

        String input = searchTxtField.getText();

        int noOfThreads = (int) threadSlider.getValue();
        System.out.println("Threads to use: " + noOfThreads);

        SearchEngineSearcher searcher = new SearchEngineSearcher(urlToFile, queue);

        Instant start = Instant.now();

        searcher.search(input, noOfThreads, downloadPath);
        queue.drainTo(searchResults);

        Instant end = Instant.now();
        Duration timeTaken = Duration.between(start, end);

        timeTakenLabel.setText("Time taken: " + timeTaken.toMillis() + "ms");
        // <editor-fold desc="Old code" defaultstate="collapsed">
//
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//
//        List<SearchResult> searchResults = new ArrayList<>();
//
//        Callable<List<String>> googleCrawl = () -> { // Google
//            final String uri = "https://www.google.com.sg/search?q=" + query + "&num=2";
//
//            Document doc = new Document(uri);
//            try {
//                doc = Jsoup.connect(uri).get();
//            } catch (IOException ex) {
//                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            // While JSoup allows for queries JQuery style, we need to use regex
//            // for assignment so we parse the body string. This retrieves the
//            // uris from the search results
//            String docBody = doc.body().toString();
//            String regex = "<h3.*?><a href=\"([^\\\\/].*?)\"";
//            Pattern p = Pattern.compile(regex, Pattern.DOTALL);
//            Matcher m = p.matcher(docBody);
//            List<String> googleSeeds = new ArrayList<>();
//            if (m.find()) {
//                do {
//                    googleSeeds.add(m.group(1));
////                    seeds.add(m.group(1));
//                    System.out.println("Google seed: " + m.group(1));
//                } while (m.find(m.start(1)));
//
//            }
//            return googleSeeds;
////            return seeds;
//            // <editor-fold desc="jsoup reference code" defaultstate="collapsed">
////        Elements googleResults = doc.body().select("h3.r > a");
////        googleResults.stream()
////                .map(googleResult -> googleResult.attr("href"))
////                .forEach(seeds::add);
////        webEngine.load(seeds.get(0));
//            // </editor-fold>
//        };
//
//        Callable<List<String>> bingCrawl = () -> { // Google
//            final String uri = "https://www.bing.com.sg/search?q=" + query;
//
//            Document doc = new Document(uri);
//            try {
//                doc = Jsoup.connect(uri).get();
//            } catch (IOException ex) {
//                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            String docBody = doc.body().toString();
//            String regex = "<a href=\"([^\\\\/].*?)\"";
//            Pattern p = Pattern.compile(regex, Pattern.DOTALL);
//            Matcher m = p.matcher(docBody);
//            int count = 0;
//            List<String> bingSeeds = new ArrayList<>();
//            if (m.find()) {
//                do {
//                    bingSeeds.add(m.group(1));
//                    System.out.println("Bing seed: " + m.group(1));
//                    count++;
//                } while (m.find(m.start(1)) && count < 2);
//            }
//            return bingSeeds;
//        };
//        Collection<Callable<List<String>>> callables
//                = Arrays.asList(googleCrawl, bingCrawl);
//        List<Callable<List<SearchResult>>> callables = new ArrayList<>();
////        callables.add(() -> new GoogleCrawler().getSearchResults(query));
//
//        try {
////            List<Future<List<SearchResult>>> results = executor.invokeAll(callables);
//    
//            System.out.println("Started crawling");
//
//            executor.invokeAll(callables)
//                    .stream()
//                    .map(future -> {
//                        List<SearchResult> results = null;
//                        try {
//                            results = future.get();
//                        } catch (InterruptedException | ExecutionException e) {
//                            throw new IllegalStateException();
//                        }
//                        return results;
//                    })
//                    .filter(results -> results != null)
//                    .forEach(searchResults::addAll);
//
//            searchResults.forEach(System.out::println);
//            
//            executor.shutdown();
//            executor.awaitTermination(10, TimeUnit.SECONDS);
////            webEngine.load(seeds.peek()); // code broken for now crashes app
//        } catch (InterruptedException e) {
//            System.err.println("tasks interrupted");
//        } finally {
//            if (!executor.isTerminated()) {
//                System.err.println("cancel non-finished tasks");
//            }
//            executor.shutdownNow();
//        }
        // </editor-fold>
    }

    @FXML
    private void changeHtmlDirectory(ActionEvent event) {

        Stage stage = (Stage) changeDirButton.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose HTML Save Location");

        final File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            downloadPath = selectedDirectory.getAbsolutePath();
            pathLabel.setText(downloadPath);
        }
    }

    @FXML
    private void displayResultDetails(MouseEvent event) {

        SearchResult sr = resultsListView.getSelectionModel().getSelectedItem();

        occurancesLabel.setText("Number of occurances: " + sr.getOccurance());
        descriptionLabel.setText("Description: " + sr.getDescription());

        String htmlFile = urlToFile.get(sr.getUrl()).toURI().toString();
        System.out.println(htmlFile);
        webEngine.load(htmlFile);
    }

}

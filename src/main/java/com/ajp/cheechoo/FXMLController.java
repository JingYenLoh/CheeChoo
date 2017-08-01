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
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FXMLController implements Initializable {

    //<editor-fold desc="FXML controls" defaultstate="collapsed">
    // General controls
    @FXML
    private Text htmlText;
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
    private Text descriptionText;

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
        String formattedTime = String.format("%.1fs", timeTaken.toMillis() / 1000D);

        timeTakenLabel.setText("Time taken: " + formattedTime);
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
        descriptionText.setText("Description: " + sr.getDescription());

        String htmlFile = urlToFile.get(sr.getUrl()).toURI().toString();
        System.out.println(htmlFile);
        webEngine.load(htmlFile);
        htmlText.setText(sr.getHtml());
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ajp.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Loh Jing Yen
 */
public class SearchResult {

    public enum SearchEngine {
        GOOGLE,
        BING
    }

    private String url;
    private String html;
    private String title;
    private int occurance;
    private String description;
    private String searchedTerm;
    private List<SearchEngine> searchedBy;

    public SearchResult() {
        searchedBy = new ArrayList<>();
    }

    // <editor-fold desc="Getters and Setters" defaultstate="collapsed">
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOccurance() {
        return occurance;
    }

    public void setOccurance(int occurance) {
        this.occurance = occurance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SearchEngine> getSearchedBy() {
        return searchedBy;
    }

    public void setSearchedBy(List<SearchEngine> searchedBy) {
        this.searchedBy = searchedBy;
    }

    public String getSearchedTerm() {
        return searchedTerm;
    }

    public void setSearchedTerm(String searchedTerm) {
        this.searchedTerm = searchedTerm;
    }
    // </editor-fold>

    @Override
    public String toString() {
        return url;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchResult other = (SearchResult) obj;
        return Objects.equals(this.url, other.url);
    }
    
}

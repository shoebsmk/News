package com.example.news;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Source implements Serializable, Comparable<Source>{
    private String id;
    private String name;
    private String category;
    private String language;
    private String country;

    public Source(String id, String name, String category, String language, String country) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.language = language;
        this.country = country;
    }



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    @NonNull
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Source o) {
        return name.compareTo(o.name);
    }
}

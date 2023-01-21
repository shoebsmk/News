package com.example.news;

public class CountryCode {
    private final String code;
    private final String name;

    public CountryCode(String code, String name) {
        this.code = code.toLowerCase();
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

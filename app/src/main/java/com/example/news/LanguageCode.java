package com.example.news;

public class LanguageCode {
    private final String code;
    private final String name;

    public LanguageCode(String code, String name) {
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

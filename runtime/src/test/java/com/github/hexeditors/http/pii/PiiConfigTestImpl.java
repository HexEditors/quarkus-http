package com.github.hexeditors.http.pii;

import java.util.Map;

public class PiiConfigTestImpl implements PiiConfig {

    @Override
    public Map<String, String> headers() {
        return Map.of();
    }

    @Override
    public Map<String, String> json() {
        return Map.of("password", "SECRET", "card", "SECRET");
    }

    @Override
    public String mask() {
        return "****";
    }
}

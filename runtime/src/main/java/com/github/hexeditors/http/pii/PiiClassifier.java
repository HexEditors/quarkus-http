package com.github.hexeditors.http.pii;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

/**
 * Classifier for determining PII sensitivity levels of headers and JSON fields based on configuration.
 */
@ApplicationScoped
public class PiiClassifier {

    @Inject
    PiiConfig config;

    /**
     * Classifies the PII level of an HTTP header name.
     *
     * @param name the header name to classify
     * @return the PII level, or NONE if not configured
     */
    public PiiLevel classifyHeader(String name) {
        return resolve(name, config.headers());
    }

    /**
     * Classifies the PII level of a JSON field path.
     *
     * @param field the JSON field path to classify
     * @return the PII level, or NONE if not configured
     */
    public PiiLevel classifyJsonField(String field) {
        return resolve(field, config.json());
    }

    /**
     * Resolves the PII level for a given key from the rules map.
     *
     * @param key   the key to look up
     * @param rules the map of keys to PII level strings
     * @return the resolved PII level, or NONE if not found
     */
    private PiiLevel resolve(String key, Map<String, String> rules) {
        return rules.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(key))
                .map(e -> PiiLevel.valueOf(e.getValue()))
                .findFirst()
                .orElse(PiiLevel.NONE);
    }
}

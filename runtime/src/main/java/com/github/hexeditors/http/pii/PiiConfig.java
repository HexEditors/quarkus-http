package com.github.hexeditors.http.pii;

import io.smallrye.config.ConfigMapping;

import java.util.Map;

/**
 * Configuration interface for PII detection and masking, mapped from properties with prefix "http.pii".
 */
@ConfigMapping(prefix = "http.pii")
public interface PiiConfig {

    /**
     * Map of header names to their PII levels.
     *
     * @return map of header name to PII level string
     */
    Map<String, String> headers();

    /**
     * Map of JSON field paths to their PII levels.
     *
     * @return map of JSON path to PII level string
     */
    Map<String, String> json();

    /**
     * The string used to mask sensitive values.
     *
     * @return the mask string, defaults to "***"
     */
    @io.smallrye.config.WithDefault("***")
    String mask();
}

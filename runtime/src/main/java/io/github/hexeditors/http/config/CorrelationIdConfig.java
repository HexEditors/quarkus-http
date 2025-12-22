package io.github.hexeditors.http.config;

import io.smallrye.config.ConfigMapping;

/**
 * Configuration interface for correlation ID functionality, mapped from properties with prefix "http.correlation-id".
 */
@ConfigMapping(prefix = "http.correlation-id")
public interface CorrelationIdConfig {

    /**
     * The header name used for correlation ID.
     *
     * @return the header name, defaults to "X-Correlation-Id"
     */
    @io.smallrye.config.WithDefault("X-Correlation-Id")
    String header();
}

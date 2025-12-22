package io.github.hexeditors.http.util;

import io.github.hexeditors.http.config.CorrelationIdConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utility for managing correlation IDs used for request tracing and logging.
 * Stores correlation IDs in MDC (Mapped Diagnostic Context) for logging purposes.
 */
@ApplicationScoped
public class CorrelationIdUtil {

    @Inject
    CorrelationIdConfig config;

    /**
     * Gets the configured header name for correlation ID.
     *
     * @return the header name
     */
    public String getHeaderName() {
        return config.header();
    }

    /**
     * Gets an existing correlation ID or creates a new one if none exists.
     * Checks incoming header first, then MDC, and generates a UUID if neither exists.
     * Stores the correlation ID in MDC for logging.
     *
     * @param incoming the correlation ID from the incoming request header, may be null
     * @return the correlation ID to use
     */
    public String getOrCreate(String incoming) {
        String headerName = config.header();
        String cid = incoming != null ? incoming : MDC.get(headerName);
        if (cid == null) {
            cid = UUID.randomUUID().toString();
        }
        MDC.put(headerName, cid);
        return cid;
    }
}

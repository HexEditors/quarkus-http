package com.github.hexeditors.http.api;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * Represents the response from an HTTP request, including status code, headers, body, success flag, and correlation ID.
 *
 * @param <T> the type of the response body
 */
@Value
@Builder
public class HttpResponse<T> {
    /**
     * The HTTP status code of the response.
     */
    int statusCode;
    /**
     * The headers returned in the response.
     */
    Map<String, String> headers;
    /**
     * The deserialized response body.
     */
    T body;
    /**
     * Indicates whether the request was successful (status code 2xx).
     */
    boolean success;
    /**
     * The correlation ID for tracking the request.
     */
    String correlationId;
}

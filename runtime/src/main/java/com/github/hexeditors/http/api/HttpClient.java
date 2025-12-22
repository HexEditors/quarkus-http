package com.github.hexeditors.http.api;

import java.util.Map;

/**
 * Interface for performing HTTP client operations such as GET, POST, PUT, PATCH, and DELETE requests.
 * Implementations of this interface handle the low-level details of HTTP communication.
 */
public interface HttpClient {

    /**
     * Performs an HTTP GET request to the specified URL.
     *
     * @param <T>          the type of the response body
     * @param url          the URL to send the GET request to
     * @param params       query parameters to include in the request
     * @param headers      headers to include in the request
     * @param responseType the class type of the expected response body for deserialization
     * @return the HTTP response containing status, headers, and deserialized body
     */
    <T> HttpResponse<T> get(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Class<T> responseType
    );

    /**
     * Performs an HTTP POST request to the specified URL with the provided body.
     *
     * @param <T>          the type of the response body
     * @param url          the URL to send the POST request to
     * @param params       query parameters to include in the request
     * @param headers      headers to include in the request
     * @param body         the request body to send
     * @param responseType the class type of the expected response body for deserialization
     * @return the HTTP response containing status, headers, and deserialized body
     */
    <T> HttpResponse<T> post(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            Class<T> responseType
    );

    /**
     * Performs an HTTP PUT request to the specified URL with the provided body.
     *
     * @param <T>          the type of the response body
     * @param url          the URL to send the PUT request to
     * @param params       query parameters to include in the request
     * @param headers      headers to include in the request
     * @param body         the request body to send
     * @param responseType the class type of the expected response body for deserialization
     * @return the HTTP response containing status, headers, and deserialized body
     */
    <T> HttpResponse<T> put(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            Class<T> responseType
    );

    /**
     * Performs an HTTP PATCH request to the specified URL with the provided body.
     *
     * @param <T>          the type of the response body
     * @param url          the URL to send the PATCH request to
     * @param params       query parameters to include in the request
     * @param headers      headers to include in the request
     * @param body         the request body to send
     * @param responseType the class type of the expected response body for deserialization
     * @return the HTTP response containing status, headers, and deserialized body
     */
    <T> HttpResponse<T> patch(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            Class<T> responseType
    );

    /**
     * Performs an HTTP DELETE request to the specified URL.
     *
     * @param <T>          the type of the response body
     * @param url          the URL to send the DELETE request to
     * @param params       query parameters to include in the request
     * @param headers      headers to include in the request
     * @param responseType the class type of the expected response body for deserialization
     * @return the HTTP response containing status, headers, and deserialized body
     */
    <T> HttpResponse<T> delete(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Class<T> responseType
    );
}

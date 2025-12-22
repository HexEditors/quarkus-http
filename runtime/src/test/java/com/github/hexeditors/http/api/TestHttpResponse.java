package com.github.hexeditors.http.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestHttpResponse {

    @Test
    void testBuilder() {
        Map<String, String> headers = Map.of("Content-Type", "application/json");
        String body = "{\"message\":\"hello\"}";
        String correlationId = "12345";

        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(200)
                .headers(headers)
                .body(body)
                .success(true)
                .correlationId(correlationId)
                .build();

        assertEquals(200, response.getStatusCode());
        assertEquals(headers, response.getHeaders());
        assertEquals(body, response.getBody());
        assertTrue(response.isSuccess());
        assertEquals(correlationId, response.getCorrelationId());
    }

    @Test
    void testSuccessResponse() {
        HttpResponse<Void> response = HttpResponse.<Void>builder()
                .statusCode(204)
                .success(true)
                .build();

        assertTrue(response.isSuccess());
        assertEquals(204, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testErrorResponse() {
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(404)
                .body("Not Found")
                .success(false)
                .build();

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        assertEquals("Not Found", response.getBody());
    }

    @Test
    void testNullValues() {
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(500)
                .success(false)
                .build();

        assertNull(response.getHeaders());
        assertNull(response.getBody());
        assertNull(response.getCorrelationId());
    }

    @Test
    void testEmptyHeaders() {
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(200)
                .headers(Map.of())
                .body("OK")
                .success(true)
                .build();

        assertNotNull(response.getHeaders());
        assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    void testEquality() {
        Map<String, String> headers = Map.of("Content-Type", "application/json");
        String body = "{\"message\":\"hello\"}";
        String correlationId = "12345";

        HttpResponse<String> response1 = HttpResponse.<String>builder()
                .statusCode(200)
                .headers(headers)
                .body(body)
                .success(true)
                .correlationId(correlationId)
                .build();

        HttpResponse<String> response2 = HttpResponse.<String>builder()
                .statusCode(200)
                .headers(headers)
                .body(body)
                .success(true)
                .correlationId(correlationId)
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, HttpResponse.<String>builder().statusCode(404).build());
    }

    @Test
    void testToString() {
        HttpResponse<Void> response = HttpResponse.<Void>builder()
                .statusCode(200)
                .success(true)
                .build();

        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("HttpResponse"));
    }
}

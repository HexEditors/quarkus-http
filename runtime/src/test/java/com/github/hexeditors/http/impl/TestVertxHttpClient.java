package com.github.hexeditors.http.impl;

import com.github.hexeditors.http.api.HttpResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TestVertxHttpClient {

    private static WireMockServer wireMockServer;

    @Inject
    VertxHttpClient client;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);

        // Stub for GET /status/500
        wireMockServer.stubFor(get(urlEqualTo("/status/500"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Stub for GET /get
        wireMockServer.stubFor(get(urlEqualTo("/get"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"message\": \"success\"}")));

        // Stub for POST /post
        wireMockServer.stubFor(post(urlEqualTo("/post"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"message\": \"posted\"}")));

        // Stub for PUT /put
        wireMockServer.stubFor(put(urlEqualTo("/put"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"message\": \"put\"}")));

        // Stub for DELETE /delete
        wireMockServer.stubFor(delete(urlEqualTo("/delete"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"message\": \"deleted\"}")));
    }

    @AfterAll
    static void teardown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void returnsHttpResponseEvenFor500() {

        HttpResponse<String> response =
                client.get(
                        "http://localhost:8089/status/500",
                        Map.of(),
                        Map.of(),
                        String.class
                );

        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    void testGetSuccess() {
        HttpResponse<String> response =
                client.get(
                        "http://localhost:8089/get",
                        Map.of(),
                        Map.of(),
                        String.class
                );

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
    }

    @Test
    void testPost() {
        HttpResponse<String> response =
                client.post(
                        "http://localhost:8089/post",
                        Map.of(),
                        Map.of(),
                        "test body",
                        String.class
                );

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
    }

    @Test
    void testPut() {
        HttpResponse<String> response =
                client.put(
                        "http://localhost:8089/put",
                        Map.of(),
                        Map.of(),
                        "test body",
                        String.class
                );

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
    }

    @Test
    void testDelete() {
        HttpResponse<String> response =
                client.delete(
                        "http://localhost:8089/delete",
                        Map.of(),
                        Map.of(),
                        String.class
                );

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
    }
}

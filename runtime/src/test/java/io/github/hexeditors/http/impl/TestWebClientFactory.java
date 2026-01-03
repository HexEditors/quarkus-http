package io.github.hexeditors.http.impl;

import io.github.hexeditors.http.config.HttpClientConfig;
import io.github.hexeditors.http.config.TlsConfig;
import io.github.hexeditors.http.config.TrustStoreConfig;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestWebClientFactory {

    @Mock
    private Vertx vertx;
    @Mock
    private TlsConfig tlsConfig;
    @Mock
    private HttpClientConfig httpClientConfig;
    @Mock
    private WebClient webClient;

    private WebClientFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        factory = new WebClientFactory();
        factory.vertx = vertx;
        factory.tls = tlsConfig;
        factory.httpClientConfig = httpClientConfig;
    }

    @Test
    void testCreate_InsecureDomain() {
        // Arrange
        String host = "localhost";
        when(tlsConfig.insecureDomains()).thenReturn(Set.of("localhost", "127.0.0.1"));

        try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            mockedWebClient.when(() -> WebClient.create(any(Vertx.class), any(WebClientOptions.class)))
                    .thenReturn(webClient);

            // Act
            WebClient result = factory.create(host);

            // Assert
            assertEquals(webClient, result);
            mockedWebClient.verify(() -> WebClient.create(eq(vertx), argThat(options -> {
                // Should have trustAll and verifyHost disabled for insecure domains
                return options.isTrustAll() && !options.isVerifyHost();
            })));
        }
    }

    @Test
    void testCreate_WithTrustStoreConfig() {
        // Arrange
        String host = "api.example.com";
        when(tlsConfig.insecureDomains()).thenReturn(Set.of("localhost"));
        when(tlsConfig.trustStores()).thenReturn(Map.of(
            "api.example.com", createTrustStoreConfig("certs/api.jks", "password123")
        ));

        try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            mockedWebClient.when(() -> WebClient.create(any(Vertx.class), any(WebClientOptions.class)))
                    .thenReturn(webClient);

            // Act
            WebClient result = factory.create(host);

            // Assert
            assertEquals(webClient, result);
            mockedWebClient.verify(() -> WebClient.create(eq(vertx), argThat(options -> {
                // Should not have trustAll set and should have trust store configured
                return !options.isTrustAll();
            })));
        }
    }

    @Test
    void testCreate_WithWildcardTrustStoreConfig() {
        // Arrange
        String host = "sub.example.com";
        when(tlsConfig.insecureDomains()).thenReturn(Set.of("localhost"));
        when(tlsConfig.trustStores()).thenReturn(Map.of(
            "*.example.com", createTrustStoreConfig("certs/wildcard.jks", "password123")
        ));

        try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            mockedWebClient.when(() -> WebClient.create(any(Vertx.class), any(WebClientOptions.class)))
                    .thenReturn(webClient);

            // Act
            WebClient result = factory.create(host);

            // Assert
            assertEquals(webClient, result);
            mockedWebClient.verify(() -> WebClient.create(eq(vertx), argThat(options -> !options.isTrustAll())));
        }
    }

    @Test
    void testCreate_WithProxyConfig() {
        // Arrange
        String host = "api.example.com";
        when(tlsConfig.insecureDomains()).thenReturn(Set.of("localhost"));
        when(tlsConfig.trustStores()).thenReturn(Map.of());
        when(httpClientConfig.proxyHost()).thenReturn(Optional.of("proxy.company.com"));
        when(httpClientConfig.proxyPort()).thenReturn(8080);
        when(httpClientConfig.proxyDomains()).thenReturn(Optional.of(Set.of("api.example.com")));

        try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            mockedWebClient.when(() -> WebClient.create(any(Vertx.class), any(WebClientOptions.class)))
                    .thenReturn(webClient);

            // Act
            WebClient result = factory.create(host);

            // Assert
            assertEquals(webClient, result);
            mockedWebClient.verify(() -> WebClient.create(eq(vertx), argThat(options -> {
                ProxyOptions proxy = options.getProxyOptions();
                return proxy != null &&
                       "proxy.company.com".equals(proxy.getHost()) &&
                       proxy.getPort() == 8080;
            })));
        }
    }

    @Test
    void testCreate_NoTrustStoreConfig() {
        // Arrange
        String host = "api.example.com";
        when(tlsConfig.insecureDomains()).thenReturn(Set.of("localhost"));
        when(tlsConfig.trustStores()).thenReturn(Map.of());

        try (MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            mockedWebClient.when(() -> WebClient.create(any(Vertx.class), any(WebClientOptions.class)))
                    .thenReturn(webClient);

            // Act
            WebClient result = factory.create(host);

            // Assert
            assertEquals(webClient, result);
            mockedWebClient.verify(() -> WebClient.create(eq(vertx), argThat(options -> {
                // Should not have trustAll and no trust store configured (uses JVM default)
                return !options.isTrustAll();
            })));
        }
    }

    @Test
    void testFindTrustStoreForHost_ExactMatch() {
        // Arrange
        String host = "api.example.com";
        Map<String, TrustStoreConfig> trustStores = Map.of(
            "api.example.com", createTrustStoreConfig("certs/api.jks", "pass1"),
            "*.example.com", createTrustStoreConfig("certs/wildcard.jks", "pass2")
        );
        when(tlsConfig.trustStores()).thenReturn(trustStores);

        // Act
        TrustStoreConfig result = factory.findTrustStoreForHost(host);

        // Assert
        assertNotNull(result);
        assertEquals("certs/api.jks", result.path());
        assertEquals("pass1", result.password());
    }

    @Test
    void testFindTrustStoreForHost_WildcardMatch() {
        // Arrange
        String host = "sub.example.com";
        Map<String, TrustStoreConfig> trustStores = Map.of(
            "api.example.com", createTrustStoreConfig("certs/api.jks", "pass1"),
            "*.example.com", createTrustStoreConfig("certs/wildcard.jks", "pass2")
        );
        when(tlsConfig.trustStores()).thenReturn(trustStores);

        // Act
        TrustStoreConfig result = factory.findTrustStoreForHost(host);

        // Assert
        assertNotNull(result);
        assertEquals("certs/wildcard.jks", result.path());
        assertEquals("pass2", result.password());
    }

    @Test
    void testFindTrustStoreForHost_NoMatch() {
        // Arrange
        String host = "other.com";
        Map<String, TrustStoreConfig> trustStores = Map.of(
            "api.example.com", createTrustStoreConfig("certs/api.jks", "pass1"),
            "*.example.com", createTrustStoreConfig("certs/wildcard.jks", "pass2")
        );
        when(tlsConfig.trustStores()).thenReturn(trustStores);

        // Act
        TrustStoreConfig result = factory.findTrustStoreForHost(host);

        // Assert
        assertNull(result);
    }

    @Test
    void testFindTrustStoreForHost_EmptyTrustStores() {
        // Arrange
        String host = "api.example.com";
        when(tlsConfig.trustStores()).thenReturn(Map.of());

        // Act
        TrustStoreConfig result = factory.findTrustStoreForHost(host);

        // Assert
        assertNull(result);
    }

    @Test
    void testFindTrustStoreForHost_NullTrustStores() {
        // Arrange
        String host = "api.example.com";
        when(tlsConfig.trustStores()).thenReturn(null);

        // Act
        TrustStoreConfig result = factory.findTrustStoreForHost(host);

        // Assert
        assertNull(result);
    }

    @Test
    void testFindTrustStoreForHost_WildcardPriority() {
        // Arrange
        String host = "api.example.com";
        Map<String, TrustStoreConfig> trustStores = Map.of(
            "*.example.com", createTrustStoreConfig("certs/wildcard.jks", "wildcard"),
            "api.example.com", createTrustStoreConfig("certs/api.jks", "exact")
        );
        when(tlsConfig.trustStores()).thenReturn(trustStores);

        // Act
        TrustStoreConfig result = factory.findTrustStoreForHost(host);

        // Assert - exact match should take priority
        assertNotNull(result);
        assertEquals("certs/api.jks", result.path());
        assertEquals("exact", result.password());
    }

    @Test
    void testFindTrustStoreForHost_MultipleWildcards() {
        // Arrange
        String host = "sub.example.com";
        Map<String, TrustStoreConfig> trustStores = Map.of(
            "*.test.com", createTrustStoreConfig("certs/test.jks", "test"),
            "*.example.com", createTrustStoreConfig("certs/example.jks", "example")
        );
        when(tlsConfig.trustStores()).thenReturn(trustStores);

        // Act
        TrustStoreConfig result = factory.findTrustStoreForHost(host);

        // Assert - should match the first wildcard that applies
        assertNotNull(result);
        assertEquals("certs/example.jks", result.path());
        assertEquals("example", result.password());
    }

    private TrustStoreConfig createTrustStoreConfig(String path, String password) {
        return new TrustStoreConfig() {
            @Override
            public String path() {
                return path;
            }

            @Override
            public String password() {
                return password;
            }
        };
    }
}

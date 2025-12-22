package io.github.hexeditors.http.config;

import io.smallrye.config.ConfigMapping;

import java.util.Optional;
import java.util.Set;

/**
 * Configuration interface for HTTP client settings, mapped from properties with prefix "http.client".
 */
@ConfigMapping(prefix = "http.client")
public interface HttpClientConfig {

    /**
     * The timeout in milliseconds for HTTP requests.
     *
     * @return the timeout, defaults to 5000ms
     */
    @io.smallrye.config.WithDefault("5000")
    int timeoutMillis();

    /**
     * The maximum number of retries for failed requests.
     *
     * @return the max retries, defaults to 3
     */
    @io.smallrye.config.WithDefault("3")
    int maxRetries();

    /**
     * The threshold for the circuit breaker failure count.
     *
     * @return the circuit breaker threshold, defaults to 5
     */
    @io.smallrye.config.WithDefault("5")
    int circuitBreakerThreshold();

    /**
     * The timeout in milliseconds for the circuit breaker to reset.
     *
     * @return the circuit breaker timeout, defaults to 60000ms
     */
    @io.smallrye.config.WithDefault("60000")
    long circuitBreakerTimeoutMillis();

    /**
     * The proxy host to use for requests.
     *
     * @return the optional proxy host
     */
    Optional<String> proxyHost();

    /**
     * The proxy port to use for requests.
     *
     * @return the proxy port, defaults to 8080
     */
    @io.smallrye.config.WithDefault("8080")
    int proxyPort();

    /**
     * The domains that should use the proxy.
     *
     * @return the optional set of proxy domains
     */
    Optional<Set<String>> proxyDomains();
}

package io.github.hexeditors.http.config;

import io.smallrye.config.ConfigMapping;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Configuration interface for TLS settings, mapped from properties with prefix "http.tls".
 */
@ConfigMapping(prefix = "http.tls")
public interface TlsConfig {

    /**
     * Whether TLS is enabled for HTTP requests.
     *
     * @return true if TLS is enabled, false otherwise, defaults to true
     */
    @io.smallrye.config.WithDefault("true")
    boolean enabled();

    /**
     * Domains that should skip TLS verification (for development/testing).
     *
     * @return the set of insecure domains, defaults to localhost and 127.0.0.1
     */
    @io.smallrye.config.WithDefault("localhost,127.0.0.1")
    Set<String> insecureDomains();



    /**
     * Per-domain trust store configurations.
     * Keys can be domain names or patterns (e.g., "*.example.com").
     * Values contain the trust store path and password for that domain.
     *
     * @return map of domain to trust store configuration
     */
    Map<String, TrustStoreConfig> trustStores();
}

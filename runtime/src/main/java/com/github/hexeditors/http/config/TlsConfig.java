package com.github.hexeditors.http.config;

import io.smallrye.config.ConfigMapping;

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
     * The path to the trust store file.
     *
     * @return the trust store path, defaults to "certs/truststore.jks"
     */
    @io.smallrye.config.WithDefault("certs/truststore.jks")
    String trustStorePath();

    /**
     * The password for the trust store.
     *
     * @return the trust store password, defaults to "changeit"
     */
    @io.smallrye.config.WithDefault("changeit")
    String trustStorePassword();
}

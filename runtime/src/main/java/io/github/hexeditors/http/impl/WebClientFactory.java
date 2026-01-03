package io.github.hexeditors.http.impl;

import io.github.hexeditors.http.config.HttpClientConfig;
import io.github.hexeditors.http.config.TlsConfig;
import io.github.hexeditors.http.config.TrustStoreConfig;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.File;
import java.util.Map;

/**
 * Factory for creating Vert.x WebClient instances with configuration-based options.
 * Handles TLS settings and proxy configuration per host.
 */
@ApplicationScoped
public class WebClientFactory {

    @Inject
    Vertx vertx;
    @Inject
    TlsConfig tls;
    @Inject
    HttpClientConfig httpClientConfig;

    /**
     * Creates a WebClient configured for the specified host.
     * Applies TLS trust settings and proxy options based on configuration.
     *
     * @param host the target host for the client
     * @return a configured WebClient instance
     */
    public WebClient create(String host) {

        WebClientOptions opt = new WebClientOptions();

        if (tls.insecureDomains().contains(host)) {
            opt.setTrustAll(true);
            opt.setVerifyHost(false);
        } else {
            // Set up trust store for this host if configured
            TrustStoreConfig trustStoreConfig = findTrustStoreForHost(host);
            if (trustStoreConfig != null) {
                File trustStoreFile = new File(trustStoreConfig.path());
                if (trustStoreFile.exists()) {
                    JksOptions jksOptions = new JksOptions()
                            .setPath(trustStoreConfig.path())
                            .setPassword(trustStoreConfig.password());
                    opt.setTrustStoreOptions(jksOptions);
                }
            }
            // If no trust store configured for this host, use JVM default trust store
        }

        // Set proxy if host is in proxy domains and proxy host is configured
        if (httpClientConfig.proxyHost().isPresent() && !httpClientConfig.proxyHost().get().isEmpty()
                && httpClientConfig.proxyDomains().isPresent() && httpClientConfig.proxyDomains().get().contains(host)) {
            ProxyOptions proxyOptions = new ProxyOptions()
                    .setHost(httpClientConfig.proxyHost().get())
                    .setPort(httpClientConfig.proxyPort());
            opt.setProxyOptions(proxyOptions);
        }

        return WebClient.create(vertx, opt);
    }

    /**
     * Finds the appropriate trust store configuration for the given host.
     * First checks for exact domain matches, then checks for wildcard patterns.
     *
     * @param host the host to find trust store for
     * @return the trust store configuration, or null if not found
     */
    TrustStoreConfig findTrustStoreForHost(String host) {
        Map<String, TrustStoreConfig> trustStores = tls.trustStores();
        if (trustStores == null || trustStores.isEmpty()) {
            return null;
        }

        // Check for exact match first
        if (trustStores.containsKey(host)) {
            return trustStores.get(host);
        }

        // Check for wildcard matches
        for (Map.Entry<String, TrustStoreConfig> entry : trustStores.entrySet()) {
            String domainPattern = entry.getKey();
            if (domainPattern.startsWith("*.")) {
                String suffix = domainPattern.substring(2); // Remove "*."
                if (host.endsWith(suffix)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }
}

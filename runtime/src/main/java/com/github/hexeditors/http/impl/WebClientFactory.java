package io.github.hexeditors.http.impl;

import io.github.hexeditors.http.config.HttpClientConfig;
import io.github.hexeditors.http.config.TlsConfig;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
}

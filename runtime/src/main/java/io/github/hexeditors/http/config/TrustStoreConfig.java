package io.github.hexeditors.http.config;

import io.smallrye.config.WithDefault;

/**
 * Configuration for a trust store, including path and password.
 */
public interface TrustStoreConfig {

    /**
     * The path to the trust store file.
     *
     * @return the trust store path
     */
    String path();

    /**
     * The password for the trust store.
     *
     * @return the trust store password
     */
    String password();
}

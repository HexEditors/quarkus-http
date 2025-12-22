package io.github.hexeditors.http.audit;

import io.smallrye.config.ConfigMapping;

/**
 * Configuration interface for audit functionality, mapped from properties with prefix "http.audit".
 */
@ConfigMapping(prefix = "http.audit")
public interface AuditConfig {

    /**
     * Whether audit logging is enabled globally.
     *
     * @return true if audit is enabled, false otherwise
     */
    @io.smallrye.config.WithDefault("false")
    boolean enabled();

    /**
     * Whether GDPR-related audit logging is enabled.
     *
     * @return true if GDPR audit is enabled, false otherwise
     */
    @io.smallrye.config.WithDefault("false")
    boolean gdprEnabled();

    /**
     * Whether PCI-related audit logging is enabled.
     *
     * @return true if PCI audit is enabled, false otherwise
     */
    @io.smallrye.config.WithDefault("false")
    boolean pciEnabled();

    /**
     * The name of the service for audit events.
     *
     * @return the service name, defaults to "unknown-service"
     */
    @io.smallrye.config.WithDefault("unknown-service")
    String serviceName();
}

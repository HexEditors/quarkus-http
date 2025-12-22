package io.github.hexeditors.http.audit;

import io.github.hexeditors.http.pii.PiiLevel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.Map;

/**
 * Publishes audit events based on configuration and policy requirements.
 * Handles the logic for determining when and what audit events to publish.
 */
@ApplicationScoped
public class AuditPublisher {

    @Inject
    AuditSink sink;

    @Inject
    AuditPolicy policy;

    @Inject
    AuditConfig config;

    /**
     * Publishes audit events if required based on the current configuration and PII level.
     * Checks GDPR and PCI audit requirements and publishes events accordingly.
     *
     * @param operation     the operation being audited
     * @param method        the HTTP method
     * @param url           the request URL
     * @param status        the HTTP status code
     * @param correlationId the correlation ID for tracking
     * @param piiLevel      the PII level of the request
     */
    public void publishIfRequired(
            String operation,
            String method,
            String url,
            int status,
            String correlationId,
            PiiLevel piiLevel
    ) {
        if (!config.enabled()) {
            return;
        }

        if (config.gdprEnabled() && policy.requiresGdprAudit(piiLevel)) {
            sink.publish(buildEvent(
                    AuditCategory.GDPR,
                    operation,
                    method,
                    url,
                    status,
                    correlationId,
                    piiLevel
            ));
        }

        if (config.pciEnabled() && policy.requiresPciAudit(piiLevel)) {
            sink.publish(buildEvent(
                    AuditCategory.PCI,
                    operation,
                    method,
                    url,
                    status,
                    correlationId,
                    piiLevel
            ));
        }
    }

    /**
     * Builds an audit event with the provided details.
     *
     * @param category  the audit category
     * @param operation the operation
     * @param method    the HTTP method
     * @param url       the URL
     * @param status    the HTTP status
     * @param cid       the correlation ID
     * @param level     the PII level
     * @return the constructed audit event
     */
    private AuditEvent buildEvent(
            AuditCategory category,
            String operation,
            String method,
            String url,
            int status,
            String cid,
            PiiLevel level
    ) {
        return AuditEvent.builder()
                .timestamp(Instant.now())
                .category(category)
                .severity(policy.severity(level))
                .serviceName(config.serviceName())
                .operation(operation)
                .httpMethod(method)
                .url(url)
                .httpStatus(status)
                .correlationId(cid)
                .piiLevel(level.name())
                .metadata(Map.of())
                .build();
    }
}

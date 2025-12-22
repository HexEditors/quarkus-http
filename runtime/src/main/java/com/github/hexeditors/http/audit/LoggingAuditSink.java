package com.github.hexeditors.http.audit;

import com.google.common.flogger.FluentLogger;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Implementation of {@link AuditSink} that publishes audit events to the logging system using FluentLogger.
 * This sink logs audit events at INFO level with structured key-value pairs for easy parsing and monitoring.
 */
@ApplicationScoped
public class LoggingAuditSink implements AuditSink {

    private static final FluentLogger auditLog =
            FluentLogger.forEnclosingClass();

    /**
     * Publishes the given audit event to the logging system.
     * The event details are logged in a structured format with key-value pairs.
     *
     * @param event the audit event to publish
     */
    @Override
    public void publish(AuditEvent event) {
        auditLog.atInfo().log(
                "AUDIT category=%s severity=%s service=%s operation=%s " +
                        "method=%s url=%s status=%d piiLevel=%s cid=%s metadata=%s",
                event.getCategory(),
                event.getSeverity(),
                event.getServiceName(),
                event.getOperation(),
                event.getHttpMethod(),
                event.getUrl(),
                event.getHttpStatus(),
                event.getPiiLevel(),
                event.getCorrelationId(),
                event.getMetadata()
        );
    }
}

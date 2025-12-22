package com.github.hexeditors.http.audit;

/**
 * Interface for publishing audit events to various sinks such as logging, databases, or external systems.
 */
public interface AuditSink {
    /**
     * Publishes an audit event to the configured sink.
     *
     * @param event the audit event to publish
     */
    void publish(AuditEvent event);
}

package io.github.hexeditors.http.audit;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

/**
 * Represents an audit event containing details about an HTTP request for compliance and monitoring purposes.
 */
@Value
@Builder
public class AuditEvent {

    /**
     * The timestamp when the audit event occurred.
     */
    Instant timestamp;
    /**
     * The audit category of the event.
     */
    AuditCategory category;
    /**
     * The severity level of the event.
     */
    AuditSeverity severity;

    /**
     * The name of the service that generated the event.
     */
    String serviceName;
    /**
     * The operation or action that was performed.
     */
    String operation;

    /**
     * The correlation ID for tracking the request.
     */
    String correlationId;
    /**
     * The HTTP method used in the request.
     */
    String httpMethod;
    /**
     * The URL of the request.
     */
    String url;
    /**
     * The HTTP status code of the response.
     */
    int httpStatus;

    /**
     * The PII (Personally Identifiable Information) level associated with the event.
     */
    String piiLevel;
    /**
     * Additional metadata associated with the event.
     */
    Map<String, String> metadata;
}

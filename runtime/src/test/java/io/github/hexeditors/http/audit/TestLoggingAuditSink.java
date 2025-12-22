package io.github.hexeditors.http.audit;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TestLoggingAuditSink {

    private final LoggingAuditSink sink = new LoggingAuditSink();

    @Test
    void testPublish() {
        AuditEvent event = AuditEvent.builder()
                .timestamp(Instant.now())
                .category(AuditCategory.GDPR)
                .severity(AuditSeverity.INFO)
                .serviceName("test-service")
                .operation("GET")
                .correlationId("cid-123")
                .httpMethod("GET")
                .url("http://example.com")
                .httpStatus(200)
                .piiLevel("LOW")
                .metadata(Map.of("key", "value"))
                .build();

        // Should not throw any exceptions
        assertDoesNotThrow(() -> sink.publish(event));
    }

    @Test
    void testPublishWithNullMetadata() {
        AuditEvent event = AuditEvent.builder()
                .timestamp(Instant.now())
                .category(AuditCategory.PCI)
                .severity(AuditSeverity.WARN)
                .serviceName("test-service")
                .operation("POST")
                .correlationId("cid-456")
                .httpMethod("POST")
                .url("http://example.com/api")
                .httpStatus(201)
                .piiLevel("HIGH")
                .build();

        // Should handle null metadata gracefully
        assertDoesNotThrow(() -> sink.publish(event));
    }

    @Test
    void testPublishWithEmptyMetadata() {
        AuditEvent event = AuditEvent.builder()
                .timestamp(Instant.now())
                .category(AuditCategory.GDPR)
                .severity(AuditSeverity.CRITICAL)
                .serviceName("test-service")
                .operation("DELETE")
                .correlationId("cid-789")
                .httpMethod("DELETE")
                .url("http://example.com/resource")
                .httpStatus(204)
                .piiLevel("SECRET")
                .metadata(Map.of())
                .build();

        // Should handle empty metadata gracefully
        assertDoesNotThrow(() -> sink.publish(event));
    }
}

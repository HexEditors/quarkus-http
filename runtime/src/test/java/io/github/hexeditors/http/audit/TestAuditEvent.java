package io.github.hexeditors.http.audit;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestAuditEvent {

    @Test
    void testBuilder() {
        Instant timestamp = Instant.now();
        Map<String, String> metadata = Map.of("key", "value");

        AuditEvent event = AuditEvent.builder()
                .timestamp(timestamp)
                .category(AuditCategory.GDPR)
                .severity(AuditSeverity.INFO)
                .serviceName("test-service")
                .operation("GET")
                .correlationId("cid-123")
                .httpMethod("GET")
                .url("http://example.com")
                .httpStatus(200)
                .piiLevel("LOW")
                .metadata(metadata)
                .build();

        assertEquals(timestamp, event.getTimestamp());
        assertEquals(AuditCategory.GDPR, event.getCategory());
        assertEquals(AuditSeverity.INFO, event.getSeverity());
        assertEquals("test-service", event.getServiceName());
        assertEquals("GET", event.getOperation());
        assertEquals("cid-123", event.getCorrelationId());
        assertEquals("GET", event.getHttpMethod());
        assertEquals("http://example.com", event.getUrl());
        assertEquals(200, event.getHttpStatus());
        assertEquals("LOW", event.getPiiLevel());
        assertEquals(metadata, event.getMetadata());
    }

    @Test
    void testNullValues() {
        AuditEvent event = AuditEvent.builder()
                .category(AuditCategory.PCI)
                .severity(AuditSeverity.WARN)
                .build();

        assertNull(event.getTimestamp());
        assertNull(event.getServiceName());
        assertNull(event.getCorrelationId());
        assertNull(event.getMetadata());
    }

    @Test
    void testEmptyMetadata() {
        AuditEvent event = AuditEvent.builder()
                .category(AuditCategory.GDPR)
                .severity(AuditSeverity.CRITICAL)
                .metadata(Map.of())
                .build();

        assertNotNull(event.getMetadata());
        assertTrue(event.getMetadata().isEmpty());
    }

    @Test
    void testEquality() {
        Instant timestamp = Instant.now();
        Map<String, String> metadata = Map.of("key", "value");

        AuditEvent event1 = AuditEvent.builder()
                .timestamp(timestamp)
                .category(AuditCategory.GDPR)
                .severity(AuditSeverity.INFO)
                .serviceName("test-service")
                .operation("GET")
                .correlationId("cid-123")
                .httpMethod("GET")
                .url("http://example.com")
                .httpStatus(200)
                .piiLevel("LOW")
                .metadata(metadata)
                .build();

        AuditEvent event2 = AuditEvent.builder()
                .timestamp(timestamp)
                .category(AuditCategory.GDPR)
                .severity(AuditSeverity.INFO)
                .serviceName("test-service")
                .operation("GET")
                .correlationId("cid-123")
                .httpMethod("GET")
                .url("http://example.com")
                .httpStatus(200)
                .piiLevel("LOW")
                .metadata(metadata)
                .build();

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, AuditEvent.builder().category(AuditCategory.PCI).build());
    }

    @Test
    void testToString() {
        AuditEvent event = AuditEvent.builder()
                .category(AuditCategory.GDPR)
                .severity(AuditSeverity.INFO)
                .build();

        String str = event.toString();
        assertNotNull(str);
        assertTrue(str.contains("AuditEvent"));
    }
}

package io.github.hexeditors.http.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestAuditSeverity {

    @Test
    void testEnumValues() {
        assertEquals(3, AuditSeverity.values().length);
        assertEquals(AuditSeverity.INFO, AuditSeverity.valueOf("INFO"));
        assertEquals(AuditSeverity.WARN, AuditSeverity.valueOf("WARN"));
        assertEquals(AuditSeverity.CRITICAL, AuditSeverity.valueOf("CRITICAL"));
    }

    @Test
    void testOrdinal() {
        assertEquals(0, AuditSeverity.INFO.ordinal());
        assertEquals(1, AuditSeverity.WARN.ordinal());
        assertEquals(2, AuditSeverity.CRITICAL.ordinal());
    }
}

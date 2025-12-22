package io.github.hexeditors.http.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestAuditCategory {

    @Test
    void testEnumValues() {
        assertEquals(2, AuditCategory.values().length);
        assertEquals(AuditCategory.GDPR, AuditCategory.valueOf("GDPR"));
        assertEquals(AuditCategory.PCI, AuditCategory.valueOf("PCI"));
    }

    @Test
    void testOrdinal() {
        assertEquals(0, AuditCategory.GDPR.ordinal());
        assertEquals(1, AuditCategory.PCI.ordinal());
    }
}

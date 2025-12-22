package com.github.hexeditors.http.audit;

import com.github.hexeditors.http.pii.PiiLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestAuditPolicy {

    private final AuditPolicy policy = new AuditPolicy();

    @Test
    void testRequiresGdprAudit() {
        assertFalse(policy.requiresGdprAudit(PiiLevel.NONE));
        assertFalse(policy.requiresGdprAudit(PiiLevel.LOW));
        assertTrue(policy.requiresGdprAudit(PiiLevel.MEDIUM));
        assertTrue(policy.requiresGdprAudit(PiiLevel.HIGH));
        assertTrue(policy.requiresGdprAudit(PiiLevel.SECRET));
    }

    @Test
    void testRequiresPciAudit() {
        assertFalse(policy.requiresPciAudit(PiiLevel.NONE));
        assertFalse(policy.requiresPciAudit(PiiLevel.LOW));
        assertFalse(policy.requiresPciAudit(PiiLevel.MEDIUM));
        assertTrue(policy.requiresPciAudit(PiiLevel.HIGH));
        assertTrue(policy.requiresPciAudit(PiiLevel.SECRET));
    }

    @Test
    void testSeverity() {
        assertEquals(AuditSeverity.INFO, policy.severity(PiiLevel.NONE));
        assertEquals(AuditSeverity.INFO, policy.severity(PiiLevel.LOW));
        assertEquals(AuditSeverity.INFO, policy.severity(PiiLevel.MEDIUM));
        assertEquals(AuditSeverity.WARN, policy.severity(PiiLevel.HIGH));
        assertEquals(AuditSeverity.CRITICAL, policy.severity(PiiLevel.SECRET));
    }
}

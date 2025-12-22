package com.github.hexeditors.http.audit;

import com.github.hexeditors.http.pii.PiiLevel;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Defines the audit policy based on PII (Personally Identifiable Information) levels.
 * Determines whether audit events should be generated for GDPR and PCI compliance, and assigns severity levels.
 */
@ApplicationScoped
public class AuditPolicy {

    /**
     * Determines if GDPR audit is required based on the PII level.
     * Audit is required for MEDIUM, HIGH, and SECRET levels.
     *
     * @param level the PII level
     * @return true if GDPR audit is required, false otherwise
     */
    public boolean requiresGdprAudit(PiiLevel level) {
        return level.ordinal() >= PiiLevel.MEDIUM.ordinal();
    }

    /**
     * Determines if PCI audit is required based on the PII level.
     * Audit is required for HIGH and SECRET levels.
     *
     * @param level the PII level
     * @return true if PCI audit is required, false otherwise
     */
    public boolean requiresPciAudit(PiiLevel level) {
        return level.ordinal() >= PiiLevel.HIGH.ordinal();
    }

    /**
     * Determines the audit severity based on the PII level.
     * SECRET maps to CRITICAL, HIGH to WARN, others to INFO.
     *
     * @param level the PII level
     * @return the corresponding audit severity
     */
    public AuditSeverity severity(PiiLevel level) {
        if (level == PiiLevel.SECRET) {
            return AuditSeverity.CRITICAL;
        }
        if (level == PiiLevel.HIGH) {
            return AuditSeverity.WARN;
        }
        return AuditSeverity.INFO;
    }
}

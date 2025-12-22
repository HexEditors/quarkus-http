package com.github.hexeditors.http.pii;

/**
 * Enumeration of Personally Identifiable Information (PII) sensitivity levels.
 * Levels are ordered from least sensitive (NONE) to most sensitive (SECRET).
 */
public enum PiiLevel {
    /**
     * No sensitive information.
     */
    NONE,       // safe
    /**
     * Low sensitivity: correlation IDs, request IDs.
     */
    LOW,        // correlation ids, request ids
    /**
     * Medium sensitivity: email addresses, phone numbers.
     */
    MEDIUM,     // email, phone
    /**
     * High sensitivity: API tokens, access keys.
     */
    HIGH,       // tokens, api keys
    /**
     * Maximum sensitivity: passwords, secrets, private keys.
     */
    SECRET      // passwords, secrets, private keys
}

package io.github.hexeditors.http.pii;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRegexPiiType {

    @Test
    void testEnumValues() {
        assertEquals(2, RegexPiiType.values().length);
        assertEquals(RegexPiiType.CREDIT_CARD, RegexPiiType.valueOf("CREDIT_CARD"));
        assertEquals(RegexPiiType.SSN, RegexPiiType.valueOf("SSN"));
    }

    @Test
    void testOrdinal() {
        assertEquals(0, RegexPiiType.CREDIT_CARD.ordinal());
        assertEquals(1, RegexPiiType.SSN.ordinal());
    }
}

package io.github.hexeditors.http.pii;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPiiLevel {

    @Test
    void testEnumValues() {
        assertEquals(5, PiiLevel.values().length);
        assertEquals(PiiLevel.NONE, PiiLevel.valueOf("NONE"));
        assertEquals(PiiLevel.LOW, PiiLevel.valueOf("LOW"));
        assertEquals(PiiLevel.MEDIUM, PiiLevel.valueOf("MEDIUM"));
        assertEquals(PiiLevel.HIGH, PiiLevel.valueOf("HIGH"));
        assertEquals(PiiLevel.SECRET, PiiLevel.valueOf("SECRET"));
    }

    @Test
    void testOrdinal() {
        assertEquals(0, PiiLevel.NONE.ordinal());
        assertEquals(1, PiiLevel.LOW.ordinal());
        assertEquals(2, PiiLevel.MEDIUM.ordinal());
        assertEquals(3, PiiLevel.HIGH.ordinal());
        assertEquals(4, PiiLevel.SECRET.ordinal());
    }

    @Test
    void testOrdinalOrdering() {
        assertTrue(PiiLevel.NONE.ordinal() < PiiLevel.LOW.ordinal());
        assertTrue(PiiLevel.LOW.ordinal() < PiiLevel.MEDIUM.ordinal());
        assertTrue(PiiLevel.MEDIUM.ordinal() < PiiLevel.HIGH.ordinal());
        assertTrue(PiiLevel.HIGH.ordinal() < PiiLevel.SECRET.ordinal());
    }
}

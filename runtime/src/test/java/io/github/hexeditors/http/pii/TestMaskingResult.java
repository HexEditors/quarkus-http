package io.github.hexeditors.http.pii;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestMaskingResult {

    @Test
    void testBuilder() {
        MaskingResult result = MaskingResult.builder()
                .maskedValue("***")
                .highestLevel(PiiLevel.HIGH)
                .build();

        assertEquals("***", result.getMaskedValue());
        assertEquals(PiiLevel.HIGH, result.getHighestLevel());
    }

    @Test
    void testAllLevels() {
        for (PiiLevel level : PiiLevel.values()) {
            MaskingResult result = MaskingResult.builder()
                    .maskedValue("masked-" + level.name())
                    .highestLevel(level)
                    .build();

            assertEquals("masked-" + level.name(), result.getMaskedValue());
            assertEquals(level, result.getHighestLevel());
        }
    }

    @Test
    void testNullMaskedValue() {
        MaskingResult result = MaskingResult.builder()
                .highestLevel(PiiLevel.NONE)
                .build();

        assertNull(result.getMaskedValue());
        assertEquals(PiiLevel.NONE, result.getHighestLevel());
    }

    @Test
    void testEmptyMaskedValue() {
        MaskingResult result = MaskingResult.builder()
                .maskedValue("")
                .highestLevel(PiiLevel.LOW)
                .build();

        assertEquals("", result.getMaskedValue());
        assertEquals(PiiLevel.LOW, result.getHighestLevel());
    }

    @Test
    void testEquality() {
        MaskingResult result1 = MaskingResult.builder()
                .maskedValue("masked")
                .highestLevel(PiiLevel.HIGH)
                .build();

        MaskingResult result2 = MaskingResult.builder()
                .maskedValue("masked")
                .highestLevel(PiiLevel.HIGH)
                .build();

        assertEquals(result1, result2);
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1, MaskingResult.builder().highestLevel(PiiLevel.SECRET).build());
    }

    @Test
    void testToString() {
        MaskingResult result = MaskingResult.builder()
                .maskedValue("test")
                .highestLevel(PiiLevel.MEDIUM)
                .build();

        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("MaskingResult"));
    }
}

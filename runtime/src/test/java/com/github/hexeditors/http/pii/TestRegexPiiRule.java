package com.github.hexeditors.http.pii;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TestRegexPiiRule {

    @Test
    void testRecordCreation() {
        Pattern pattern = Pattern.compile("\\d{4}-\\d{4}-\\d{4}-\\d{4}");
        RegexPiiRule rule = new RegexPiiRule(RegexPiiType.CREDIT_CARD, pattern, PiiLevel.HIGH);

        assertEquals(RegexPiiType.CREDIT_CARD, rule.type());
        assertEquals(pattern, rule.pattern());
        assertEquals(PiiLevel.HIGH, rule.level());
    }

    @Test
    void testRecordEquality() {
        Pattern pattern = Pattern.compile("test");

        RegexPiiRule rule1 = new RegexPiiRule(RegexPiiType.SSN, pattern, PiiLevel.SECRET);
        RegexPiiRule rule2 = new RegexPiiRule(RegexPiiType.SSN, pattern, PiiLevel.SECRET);

        // Records with same values should be equal
        assertEquals(rule1, rule2);
        assertEquals(rule1.hashCode(), rule2.hashCode());
    }

    @Test
    void testRecordInequality() {
        Pattern pattern = Pattern.compile("test");
        RegexPiiRule rule1 = new RegexPiiRule(RegexPiiType.CREDIT_CARD, pattern, PiiLevel.HIGH);
        RegexPiiRule rule2 = new RegexPiiRule(RegexPiiType.SSN, pattern, PiiLevel.HIGH);

        assertNotEquals(rule1, rule2);
    }

    @Test
    void testToString() {
        Pattern pattern = Pattern.compile("simple");
        RegexPiiRule rule = new RegexPiiRule(RegexPiiType.CREDIT_CARD, pattern, PiiLevel.MEDIUM);

        String toString = rule.toString();
        assertTrue(toString.contains("RegexPiiRule"));
        assertTrue(toString.contains("CREDIT_CARD"));
        assertTrue(toString.contains("MEDIUM"));
    }

    @Test
    void testAllTypesAndLevels() {
        for (RegexPiiType type : RegexPiiType.values()) {
            for (PiiLevel level : PiiLevel.values()) {
                Pattern pattern = Pattern.compile(type.name() + level.name());
                RegexPiiRule rule = new RegexPiiRule(type, pattern, level);

                assertEquals(type, rule.type());
                assertEquals(level, rule.level());
                assertEquals(pattern, rule.pattern());
            }
        }
    }
}

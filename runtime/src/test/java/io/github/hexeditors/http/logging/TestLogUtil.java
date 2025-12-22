package io.github.hexeditors.http.logging;

import io.github.hexeditors.http.pii.PiiClassifier;
import io.github.hexeditors.http.pii.PiiLevel;
import io.github.hexeditors.http.pii.RegexPiiDetector;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestLogUtil {

    @Test
    void testMaskHeadersNoMasking() {
        PiiClassifier classifier = mock(PiiClassifier.class);
        RegexPiiDetector regexDetector = mock(RegexPiiDetector.class);

        when(classifier.classifyHeader(anyString())).thenReturn(PiiLevel.NONE);
        when(regexDetector.detect(anyString())).thenReturn(PiiLevel.NONE);

        Map<String, String> headers = Map.of("Content-Type", "application/json", "Accept", "text/plain");
        Map<String, String> result = LogUtil.maskHeaders(headers, classifier, regexDetector, "***");

        assertEquals(headers, result);
    }

    @Test
    void testMaskHeadersWithMasking() {
        PiiClassifier classifier = mock(PiiClassifier.class);
        RegexPiiDetector regexDetector = mock(RegexPiiDetector.class);

        when(classifier.classifyHeader("Authorization")).thenReturn(PiiLevel.HIGH);
        when(classifier.classifyHeader("Content-Type")).thenReturn(PiiLevel.NONE);
        when(regexDetector.detect(anyString())).thenReturn(PiiLevel.NONE);

        Map<String, String> headers = Map.of("Authorization", "Bearer token123", "Content-Type", "application/json");
        Map<String, String> result = LogUtil.maskHeaders(headers, classifier, regexDetector, "***");

        assertEquals("***", result.get("Authorization"));
        assertEquals("application/json", result.get("Content-Type"));
    }

    @Test
    void testMaskHeadersRegexHigherLevel() {
        PiiClassifier classifier = mock(PiiClassifier.class);
        RegexPiiDetector regexDetector = mock(RegexPiiDetector.class);

        when(classifier.classifyHeader("X-API-Key")).thenReturn(PiiLevel.LOW);
        when(regexDetector.detect("secret-key-123")).thenReturn(PiiLevel.HIGH);

        Map<String, String> headers = Map.of("X-API-Key", "secret-key-123");
        Map<String, String> result = LogUtil.maskHeaders(headers, classifier, regexDetector, "MASKED");

        assertEquals("MASKED", result.get("X-API-Key"));
    }

    @Test
    void testMaskHeadersClassifierHigherLevel() {
        PiiClassifier classifier = mock(PiiClassifier.class);
        RegexPiiDetector regexDetector = mock(RegexPiiDetector.class);

        when(classifier.classifyHeader("Authorization")).thenReturn(PiiLevel.SECRET);
        when(regexDetector.detect("Bearer token")).thenReturn(PiiLevel.LOW);

        Map<String, String> headers = Map.of("Authorization", "Bearer token");
        Map<String, String> result = LogUtil.maskHeaders(headers, classifier, regexDetector, "****");

        assertEquals("****", result.get("Authorization"));
    }

    @Test
    void testMaskHeadersEmptyMap() {
        PiiClassifier classifier = mock(PiiClassifier.class);
        RegexPiiDetector regexDetector = mock(RegexPiiDetector.class);

        Map<String, String> headers = Map.of();
        Map<String, String> result = LogUtil.maskHeaders(headers, classifier, regexDetector, "***");

        assertTrue(result.isEmpty());
    }

    @Test
    void testIsTraceEnabled() {
        // This is hard to test without controlling the logger level
        // Just ensure it doesn't throw exceptions
        assertDoesNotThrow(() -> LogUtil.isTraceEnabled());
    }
}

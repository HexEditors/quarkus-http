package io.github.hexeditors.http.pii;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexPiiDetectorTest {

    private final RegexPiiDetector detector = new RegexPiiDetector();

    @Test
    void detectsCreditCard() {
        assertEquals(
                PiiLevel.SECRET,
                detector.detect("4111111111111111")
        );
    }

    @Test
    void detectsSSN() {
        assertEquals(
                PiiLevel.SECRET,
                detector.detect("123-45-6789")
        );
    }

    @Test
    void ignoresNormalText() {
        assertEquals(
                PiiLevel.NONE,
                detector.detect("hello-world")
        );
    }
}

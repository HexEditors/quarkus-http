package io.github.hexeditors.http.pii;

import io.github.hexeditors.http.TestInjectionUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestPiiClassifier {

    @Test
    void testClassifyHeader() {
        PiiConfig config = new PiiConfigTestImpl();
        PiiClassifier classifier = new PiiClassifier();

        TestInjectionUtil.inject(classifier, config);

        assertEquals(PiiLevel.NONE, classifier.classifyHeader("content-type"));
        assertEquals(PiiLevel.NONE, classifier.classifyHeader("unknown-header"));
    }

    @Test
    void testClassifyHeaderCaseInsensitive() {
        PiiConfig config = new PiiConfigTestImpl() {
            @Override
            public Map<String, String> headers() {
                return Map.of("Authorization", "HIGH");
            }
        };
        PiiClassifier classifier = new PiiClassifier();

        TestInjectionUtil.inject(classifier, config);

        assertEquals(PiiLevel.HIGH, classifier.classifyHeader("authorization"));
        assertEquals(PiiLevel.HIGH, classifier.classifyHeader("AUTHORIZATION"));
        assertEquals(PiiLevel.NONE, classifier.classifyHeader("different-header"));
    }

    @Test
    void testClassifyJsonField() {
        PiiConfig config = new PiiConfigTestImpl();
        PiiClassifier classifier = new PiiClassifier();

        TestInjectionUtil.inject(classifier, config);

        assertEquals(PiiLevel.SECRET, classifier.classifyJsonField("password"));
        assertEquals(PiiLevel.SECRET, classifier.classifyJsonField("card"));
        assertEquals(PiiLevel.NONE, classifier.classifyJsonField("name"));
    }

    @Test
    void testClassifyJsonFieldCaseInsensitive() {
        PiiConfig config = new PiiConfigTestImpl() {
            @Override
            public Map<String, String> json() {
                return Map.of("Email", "MEDIUM");
            }
        };
        PiiClassifier classifier = new PiiClassifier();

        TestInjectionUtil.inject(classifier, config);

        assertEquals(PiiLevel.MEDIUM, classifier.classifyJsonField("email"));
        assertEquals(PiiLevel.MEDIUM, classifier.classifyJsonField("EMAIL"));
        assertEquals(PiiLevel.NONE, classifier.classifyJsonField("username"));
    }

    @Test
    void testClassifyWithEmptyConfig() {
        PiiConfig config = new PiiConfigTestImpl() {
            @Override
            public Map<String, String> headers() {
                return Map.of();
            }

            @Override
            public Map<String, String> json() {
                return Map.of();
            }
        };
        PiiClassifier classifier = new PiiClassifier();

        TestInjectionUtil.inject(classifier, config);

        assertEquals(PiiLevel.NONE, classifier.classifyHeader("any-header"));
        assertEquals(PiiLevel.NONE, classifier.classifyJsonField("any-field"));
    }
}

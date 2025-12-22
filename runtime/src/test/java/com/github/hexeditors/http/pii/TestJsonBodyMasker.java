package io.github.hexeditors.http.pii;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hexeditors.http.TestInjectionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestJsonBodyMasker {

    @Test
    void masksSensitiveJsonFields() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonBodyMasker masker = new JsonBodyMasker();

        PiiConfig config = new PiiConfigTestImpl();
        PiiClassifier classifier = new PiiClassifier();
        RegexPiiDetector regex = new RegexPiiDetector();

        TestInjectionUtil.inject(classifier, config);
        TestInjectionUtil.inject(masker, mapper, classifier, regex, config);

        MaskingResult result =
                masker.mask(mapper.readTree("""
                          {
                            "password": "secret",
                            "card": "4111111111111111"
                          }
                        """));

        assertTrue(result.getMaskedValue().contains("****"));
        assertEquals(PiiLevel.SECRET, result.getHighestLevel());
    }

    @Test
    void masksNullBody() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonBodyMasker masker = new JsonBodyMasker();

        PiiConfig config = new PiiConfigTestImpl();
        PiiClassifier classifier = new PiiClassifier();
        RegexPiiDetector regex = new RegexPiiDetector();

        TestInjectionUtil.inject(classifier, config);
        TestInjectionUtil.inject(masker, mapper, classifier, regex, config);

        MaskingResult result = masker.mask(null);

        assertNull(result.getMaskedValue());
        assertEquals(PiiLevel.NONE, result.getHighestLevel());
    }

    @Test
    void masksNonJsonBody() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonBodyMasker masker = new JsonBodyMasker();

        PiiConfig config = new PiiConfigTestImpl();
        PiiClassifier classifier = new PiiClassifier();
        RegexPiiDetector regex = new RegexPiiDetector();

        TestInjectionUtil.inject(classifier, config);
        TestInjectionUtil.inject(masker, mapper, classifier, regex, config);

        MaskingResult result = masker.mask("plain text");

        assertEquals("<unparseable-body>", result.getMaskedValue());
        assertEquals(PiiLevel.HIGH, result.getHighestLevel());
    }
}

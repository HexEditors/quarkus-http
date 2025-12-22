package com.github.hexeditors.http.pii;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Iterator;
import java.util.Map;

/**
 * Masks sensitive information in JSON request/response bodies based on PII classification rules.
 * Recursively processes JSON objects and arrays, replacing sensitive values with mask strings.
 */
@ApplicationScoped
public class JsonBodyMasker {

    @Inject
    ObjectMapper mapper;

    @Inject
    PiiClassifier classifier;

    @Inject
    RegexPiiDetector regexDetector;

    @Inject
    PiiConfig config;

    /**
     * Masks the given object if it represents a JSON structure.
     * Returns a MaskingResult with the masked JSON string and the highest PII level detected.
     *
     * @param body the object to mask (expected to be JSON-serializable)
     * @return the masking result containing masked value and highest PII level
     */
    public MaskingResult mask(Object body) {

        if (body == null) {
            return MaskingResult.builder()
                    .maskedValue(null)
                    .highestLevel(PiiLevel.NONE)
                    .build();
        }

        try {
            JsonNode root = mapper.valueToTree(body);
            if (!root.isObject() && !root.isArray()) {
                return MaskingResult.builder()
                        .maskedValue("<unparseable-body>")
                        .highestLevel(PiiLevel.HIGH)
                        .build();
            }
            PiiLevel max = maskNode(root);

            return MaskingResult.builder()
                    .maskedValue(mapper.writeValueAsString(root))
                    .highestLevel(max)
                    .build();

        } catch (Exception e) {
            return MaskingResult.builder()
                    .maskedValue("<unparseable-body>")
                    .highestLevel(PiiLevel.HIGH)
                    .build();
        }
    }

    private PiiLevel maskNode(JsonNode node) {
        PiiLevel max = PiiLevel.NONE;

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                var entry = fields.next();
                String fieldName = entry.getKey();
                JsonNode valueNode = entry.getValue();

                // 1️⃣ Field-name based PII
                PiiLevel fieldLevel = classifier.classifyJsonField(fieldName);

                // 2️⃣ Regex-based PII (credit card, SSN)
                PiiLevel regexLevel = PiiLevel.NONE;
                if (valueNode.isTextual()) {
                    regexLevel = regexDetector.detect(valueNode.asText());
                }

                PiiLevel effective =
                        fieldLevel.ordinal() > regexLevel.ordinal()
                                ? fieldLevel
                                : regexLevel;

                if (effective.ordinal() >= PiiLevel.HIGH.ordinal()) {
                    ((ObjectNode) node).put(fieldName, config.mask());
                    max = higher(max, effective);
                } else {
                    max = higher(max, maskNode(valueNode));
                }
            }
        } else if (node.isArray()) {
            for (JsonNode n : node) {
                max = higher(max, maskNode(n));
            }
        }

        return max;
    }

    private PiiLevel higher(PiiLevel a, PiiLevel b) {
        return a.ordinal() > b.ordinal() ? a : b;
    }
}

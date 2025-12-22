package io.github.hexeditors.http.pii;

import lombok.Builder;
import lombok.Value;

/**
 * Result of PII masking operation, containing the masked value and the highest PII level detected.
 */
@Value
@Builder
public class MaskingResult {
    /**
     * The value with sensitive information masked.
     */
    String maskedValue;
    /**
     * The highest PII sensitivity level found in the original value.
     */
    PiiLevel highestLevel;
}

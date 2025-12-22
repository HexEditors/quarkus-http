package io.github.hexeditors.http.pii;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Detector for sensitive information using regex patterns.
 * Checks for credit card numbers and Social Security Numbers.
 */
@ApplicationScoped
public class RegexPiiDetector {

    private static final List<RegexPiiRule> RULES = List.of(

            // Credit Card (Visa, MC, Amex, Discover – simplified but safe)
            new RegexPiiRule(
                    RegexPiiType.CREDIT_CARD,
                    Pattern.compile(
                            "\\b(?:4[0-9]{12}(?:[0-9]{3})?" +        // Visa
                                    "|5[1-5][0-9]{14}" +                    // MasterCard
                                    "|3[47][0-9]{13}" +                     // Amex
                                    "|6(?:011|5[0-9]{2})[0-9]{12})\\b"
                    ),
                    PiiLevel.SECRET
            ),

            // US Social Security Number
            new RegexPiiRule(
                    RegexPiiType.SSN,
                    Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b"),
                    PiiLevel.SECRET
            )
    );

    /**
     * Detects PII in the given string value using configured regex rules.
     * Returns the highest PII level found, or NONE if no PII is detected.
     *
     * @param value the string to scan for PII
     * @return the detected PII level
     */
    public PiiLevel detect(String value) {
        if (value == null || value.isBlank()) {
            return PiiLevel.NONE;
        }

        for (RegexPiiRule rule : RULES) {
            if (rule.pattern().matcher(value).find()) {
                return rule.level();
            }
        }
        return PiiLevel.NONE;
    }
}

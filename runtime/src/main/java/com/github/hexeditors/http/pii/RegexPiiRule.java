package com.github.hexeditors.http.pii;

import java.util.regex.Pattern;

/**
 * Represents a rule for detecting PII using regex patterns.
 *
 * @param type    the type of PII this rule detects
 * @param pattern the compiled regex pattern to match against
 * @param level   the PII sensitivity level of matches
 */
public record RegexPiiRule(
        RegexPiiType type,
        Pattern pattern,
        PiiLevel level
) {
}

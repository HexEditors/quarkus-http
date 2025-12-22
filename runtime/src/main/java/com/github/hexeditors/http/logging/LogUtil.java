package io.github.hexeditors.http.logging;

import io.github.hexeditors.http.pii.PiiClassifier;
import io.github.hexeditors.http.pii.PiiLevel;
import io.github.hexeditors.http.pii.RegexPiiDetector;
import com.google.common.flogger.FluentLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for logging operations, including trace level checks and PII-safe header masking.
 */
public final class LogUtil {

    /**
     * The FluentLogger instance for this class.
     */
    public static final FluentLogger log = FluentLogger.forEnclosingClass();

    private LogUtil() {
    }

    /**
     * Checks if finest (trace) level logging is enabled.
     *
     * @return true if trace logging is enabled, false otherwise
     */
    public static boolean isTraceEnabled() {
        return log.atFinest().isEnabled();
    }

    /**
     * Masks sensitive information in HTTP headers based on PII classification.
     * Headers with HIGH or SECRET PII levels are masked with the provided mask string.
     *
     * @param headers       the original headers map
     * @param classifier    the PII classifier for header names
     * @param regexDetector the regex-based PII detector for header values
     * @param mask          the string to use for masking sensitive values
     * @return a new map with sensitive headers masked
     */
    public static Map<String, String> maskHeaders(
            Map<String, String> headers,
            PiiClassifier classifier,
            RegexPiiDetector regexDetector,
            String mask
    ) {
        Map<String, String> out = new HashMap<>();

        headers.forEach((k, v) -> {
            PiiLevel nameLevel = classifier.classifyHeader(k);
            PiiLevel valueLevel = regexDetector.detect(v);

            PiiLevel effective =
                    nameLevel.ordinal() > valueLevel.ordinal()
                            ? nameLevel
                            : valueLevel;

            if (effective.ordinal() >= PiiLevel.HIGH.ordinal()) {
                out.put(k, mask);
            } else {
                out.put(k, v);
            }
        });

        return out;
    }
}

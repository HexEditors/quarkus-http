package io.github.hexeditors.http.util;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TestCorrelationIdUtil {

    @Inject
    CorrelationIdUtil correlationIdUtil;

    @BeforeEach
    @AfterEach
    void clearMDC() {
        MDC.clear();
    }

    @Test
    void testGetOrCreateWithIncomingId() {
        String incomingId = "incoming-123";
        String result = correlationIdUtil.getOrCreate(incomingId);

        assertEquals("incoming-123", result);
        assertEquals("incoming-123", MDC.get(correlationIdUtil.getHeaderName()));
    }

    @Test
    void testGetOrCreateWithNullIncomingAndExistingMDC() {
        MDC.put(correlationIdUtil.getHeaderName(), "mdc-456");
        String result = correlationIdUtil.getOrCreate(null);

        assertEquals("mdc-456", result);
    }

    @Test
    void testGetOrCreateWithNullIncomingAndNoMDC() {
        String result = correlationIdUtil.getOrCreate(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result, MDC.get(correlationIdUtil.getHeaderName()));
    }

    @Test
    void testGetOrCreateGeneratesUUID() {
        String result1 = correlationIdUtil.getOrCreate(null);
        MDC.clear();
        String result2 = correlationIdUtil.getOrCreate(null);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1, result2); // Should generate different UUIDs
        assertEquals(result2, MDC.get(correlationIdUtil.getHeaderName())); // Last one should be in MDC
    }

    @Test
    void testDefaultHeaderName() {
        assertEquals("X-Correlation-Id", correlationIdUtil.getHeaderName());
    }
}

package io.github.hexeditors.http.audit;

import io.github.hexeditors.http.TestInjectionUtil;
import io.github.hexeditors.http.pii.PiiLevel;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TestAuditPublisher {

    @Test
    void publishesPciAuditForSecretPii() {

        AuditSink sink = mock(AuditSink.class);
        AuditPolicy policy = new AuditPolicy();
        AuditConfig config = new AuditConfigTestImpl();

        AuditPublisher publisher = new AuditPublisher();
        TestInjectionUtil.inject(publisher, sink, policy, config);

        publisher.publishIfRequired(
                "HTTP_CLIENT_CALL",
                "POST",
                "/login",
                200,
                "cid-123",
                PiiLevel.SECRET
        );

        verify(sink, times(2)).publish(any()); // GDPR and PCI
    }

    @Test
    void publishesGdprAuditForMediumPii() {

        AuditSink sink = mock(AuditSink.class);
        AuditPolicy policy = new AuditPolicy();
        AuditConfig config = new AuditConfigTestImpl();

        AuditPublisher publisher = new AuditPublisher();
        TestInjectionUtil.inject(publisher, sink, policy, config);

        publisher.publishIfRequired(
                "HTTP_CLIENT_CALL",
                "GET",
                "/data",
                200,
                "cid-456",
                PiiLevel.MEDIUM
        );

        verify(sink, times(1)).publish(any()); // Only GDPR
    }

    @Test
    void publishesBothAuditsForHighPii() {

        AuditSink sink = mock(AuditSink.class);
        AuditPolicy policy = new AuditPolicy();
        AuditConfig config = new AuditConfigTestImpl();

        AuditPublisher publisher = new AuditPublisher();
        TestInjectionUtil.inject(publisher, sink, policy, config);

        publisher.publishIfRequired(
                "HTTP_CLIENT_CALL",
                "PUT",
                "/update",
                200,
                "cid-789",
                PiiLevel.HIGH
        );

        verify(sink, times(2)).publish(any()); // GDPR and PCI
    }

    @Test
    void doesNotPublishForLowPii() {

        AuditSink sink = mock(AuditSink.class);
        AuditPolicy policy = new AuditPolicy();
        AuditConfig config = new AuditConfigTestImpl();

        AuditPublisher publisher = new AuditPublisher();
        TestInjectionUtil.inject(publisher, sink, policy, config);

        publisher.publishIfRequired(
                "HTTP_CLIENT_CALL",
                "GET",
                "/public",
                200,
                "cid-000",
                PiiLevel.LOW
        );

        verify(sink, never()).publish(any());
    }

    @Test
    void doesNotPublishWhenAuditDisabled() {

        AuditSink sink = mock(AuditSink.class);
        AuditPolicy policy = new AuditPolicy();
        AuditConfig config = new AuditConfig() {
            @Override
            public boolean enabled() {
                return false;
            }

            @Override
            public boolean gdprEnabled() {
                return true;
            }

            @Override
            public boolean pciEnabled() {
                return true;
            }

            @Override
            public String serviceName() {
                return "test";
            }
        };

        AuditPublisher publisher = new AuditPublisher();
        TestInjectionUtil.inject(publisher, sink, policy, config);

        publisher.publishIfRequired(
                "HTTP_CLIENT_CALL",
                "POST",
                "/secret",
                200,
                "cid-999",
                PiiLevel.SECRET
        );

        verify(sink, never()).publish(any());
    }
}

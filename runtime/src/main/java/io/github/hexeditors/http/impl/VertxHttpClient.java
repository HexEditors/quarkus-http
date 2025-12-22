package io.github.hexeditors.http.impl;

import io.github.hexeditors.http.api.HttpClient;
import io.github.hexeditors.http.api.HttpResponse;
import io.github.hexeditors.http.audit.AuditPublisher;
import io.github.hexeditors.http.config.HttpClientConfig;
import io.github.hexeditors.http.logging.LogUtil;
import io.github.hexeditors.http.pii.*;
import io.github.hexeditors.http.util.CorrelationIdUtil;
import com.google.common.flogger.FluentLogger;
import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementation of {@link HttpClient} using Vert.x WebClient.
 * Provides HTTP client functionality with features like retry logic, timeouts, request/response logging, PII masking, and audit publishing.
 */
@ApplicationScoped
public class VertxHttpClient implements HttpClient {

    private static final FluentLogger log = LogUtil.log;

    @Inject
    WebClientFactory factory;

    @Inject
    PiiClassifier piiClassifier;

    @Inject
    RegexPiiDetector regexPiiDetector;

    @Inject
    JsonBodyMasker jsonBodyMasker;

    @Inject
    PiiConfig piiConfig;

    @Inject
    AuditPublisher auditPublisher;

    @Inject
    HttpClientConfig httpClientConfig;

    @Inject
    CorrelationIdUtil correlationIdUtil;

    @Override
    public <T> HttpResponse<T> get(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Class<T> type
    ) {
        return execute(HttpMethod.GET, url, headers, null, type);
    }

    @Override
    public <T> HttpResponse<T> post(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            Class<T> type
    ) {
        return execute(HttpMethod.POST, url, headers, body, type);
    }

    @Override
    public <T> HttpResponse<T> put(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            Class<T> type
    ) {
        return execute(HttpMethod.PUT, url, headers, body, type);
    }

    @Override
    public <T> HttpResponse<T> patch(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            Class<T> type
    ) {
        return execute(HttpMethod.PATCH, url, headers, body, type);
    }

    @Override
    public <T> HttpResponse<T> delete(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Class<T> type
    ) {
        return execute(HttpMethod.DELETE, url, headers, null, type);
    }

    /**
     * Executes the HTTP request with correlation ID setup and request tracing.
     *
     * @param <T>     the type of the response body
     * @param method  the HTTP method
     * @param url     the request URL
     * @param headers the request headers
     * @param body    the request body, can be null
     * @param type    the class type for response deserialization
     * @return the HTTP response
     */
    private <T> HttpResponse<T> execute(
            HttpMethod method,
            String url,
            Map<String, String> headers,
            Object body,
            Class<T> type
    ) {
        String cid = correlationIdUtil.getOrCreate(
                headers.get(correlationIdUtil.getHeaderName())
        );

        /* ===================== TRACE REQUEST ===================== */
        if (LogUtil.isTraceEnabled()) {
            Map<String, String> safeHeaders =
                    LogUtil.maskHeaders(
                            headers,
                            piiClassifier,
                            regexPiiDetector,
                            piiConfig.mask()
                    );

            MaskingResult maskedBody =
                    jsonBodyMasker.mask(body);

            log.atFinest().log(
                    "HTTP %s %s headers=%s body=%s piiLevel=%s cid=%s",
                    method,
                    url,
                    safeHeaders,
                    maskedBody.getMaskedValue(),
                    maskedBody.getHighestLevel(),
                    cid
            );
        }

        return executeWithRetry(method, url, headers, body, type, cid, new AtomicInteger(0));
    }

    /**
     * Executes the HTTP request with retry logic, response processing, logging, and auditing.
     * Handles timeouts, retries, response deserialization, PII masking, and audit publishing.
     *
     * @param <T>     the type of the response body
     * @param method  the HTTP method
     * @param url     the request URL
     * @param headers the request headers
     * @param body    the request body, can be null
     * @param type    the class type for response deserialization
     * @param cid     the correlation ID
     * @param attempt the attempt counter for retries
     * @return the HTTP response
     */
    private <T> HttpResponse<T> executeWithRetry(
            HttpMethod method,
            String url,
            Map<String, String> headers,
            Object body,
            Class<T> type,
            String cid,
            AtomicInteger attempt
    ) {
        WebClient client = factory.create(URI.create(url).getHost());

        var req = client.requestAbs(method, url);
        headers.forEach(req::putHeader);
        req.putHeader(correlationIdUtil.getHeaderName(), cid);
        var sendUni = body == null ? req.send() : req.sendJson(body);
        return sendUni
                .ifNoItem().after(Duration.ofMillis(httpClientConfig.timeoutMillis()))
                .failWith(() -> new RuntimeException("Request timeout"))
                .onFailure().retry()
                .withBackOff(Duration.ofMillis(100))
                .atMost(httpClientConfig.maxRetries())
                .map(resp -> {
                    T entity = null;
                    try {
                        if (type != Void.class && resp.body() != null) {
                            entity = resp.bodyAsJson(type);
                        }
                    } catch (Exception ignored) {
                    }

                    MaskingResult maskedResponse = jsonBodyMasker.mask(entity);

                    /* ===================== TRACE RESPONSE ===================== */
                    if (LogUtil.isTraceEnabled()) {
                        Map<String, String> safeResponseHeaders =
                                LogUtil.maskHeaders(
                                        resp.headers().entries().stream()
                                                .collect(Collectors.toMap(
                                                        Map.Entry::getKey,
                                                        Map.Entry::getValue,
                                                        (a, b) -> b
                                                )),
                                        piiClassifier,
                                        regexPiiDetector,
                                        piiConfig.mask()
                                );

                        log.atFinest().log(
                                "HTTP RESPONSE status=%d headers=%s body=%s piiLevel=%s cid=%s attempt=%d",
                                resp.statusCode(),
                                safeResponseHeaders,
                                maskedResponse.getMaskedValue(),
                                maskedResponse.getHighestLevel(),
                                cid,
                                attempt.incrementAndGet()
                        );
                    }

                    /* ===================== GDPR / PCI AUDIT ===================== */
                    auditPublisher.publishIfRequired(
                            "HTTP_CLIENT_CALL",
                            method.name(),
                            url,
                            resp.statusCode(),
                            cid,
                            maskedResponse.getHighestLevel()
                    );

                    return HttpResponse.<T>builder()
                            .statusCode(resp.statusCode())
                            .headers(
                                    resp.headers().entries().stream()
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    Map.Entry::getValue,
                                                    (a, b) -> b
                                            ))
                            )
                            .body(entity)
                            .success(resp.statusCode() < 400)
                            .correlationId(cid)
                            .build();
                })
                .onFailure().recoverWithItem(throwable -> {
                    log.atSevere()
                            .withCause(throwable)
                            .log("HTTP infrastructure failure cid=%s attempt=%d", cid, attempt.get());

                    return HttpResponse.<T>builder()
                            .statusCode(0)
                            .success(false)
                            .correlationId(cid)
                            .build();
                })
                .await().indefinitely();
    }
}

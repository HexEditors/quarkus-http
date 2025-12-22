# Quarkus HTTP Utils Extension

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.8.4-red.svg)](https://quarkus.io/)

A production-ready, Quarkus-native HTTP utility built for platform teams. Provides a safe, enterprise-grade HTTP client with comprehensive security, observability, and compliance features.

## Key Guarantees

- ✅ **No HTTP exception flow** - Always returns `HttpResponse<T>`, never throws HTTP-related exceptions
- ✅ **Full response always returned** - Even for 5xx errors or network failures
- ✅ **Correlation-ID propagation** - Automatic MDC propagation for distributed tracing
- ✅ **OpenTelemetry compatible** - Integrates seamlessly with observability stacks
- ✅ **Retry / Timeout / Circuit Breaker** - Built-in resilience patterns using SmallRye Fault Tolerance
- ✅ **Safe TRACE-level logging** - PII-masked headers and bodies at TRACE level
- ✅ **TLS customization** - Flexible SSL/TLS configuration with trust store support
- ✅ **PII masking** - Automatic detection and masking of sensitive data
- ✅ **Audit logging** - GDPR and PCI compliance audit trails

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
- [Configuration](#configuration)
- [Features](#features)
- [Logging](#logging)
- [Security & Compliance](#security--compliance)
- [Use Cases](#use-cases)
- [Building & Testing](#building--testing)
- [Contributing](#contributing)
- [License](#license)

## Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.example.platform</groupId>
    <artifactId>quarkus-http-utils</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add to your `build.gradle`:

```gradle
implementation 'com.example.platform:quarkus-http-utils:1.0.0'
```

## Quick Start

```java
package com.example;

import com.example.http.api.HttpClient;
import com.example.http.api.HttpResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.Map;

@Path("/api")
public class MyResource {

    @Inject
    HttpClient httpClient;

    @GET
    @Path("/call-external")
    public String callExternalService() {
        HttpResponse<Map> response = httpClient.get(
            "https://api.example.com/data",
            Map.of("param1", "value1"),
            Map.of("Authorization", "Bearer token"),
            Map.class
        );

        if (response.getStatusCode() >= 400) {
            return "Error: " + response.getStatusCode();
        }

        return "Success: " + response.getBody();
    }
}
```

## API Reference

### HttpClient Interface

```java
public interface HttpClient {

    // HTTP GET
    <T> HttpResponse<T> get(
        String url,
        Map<String, String> params,
        Map<String, String> headers,
        Class<T> responseType
    );

    // HTTP POST
    <T> HttpResponse<T> post(
        String url,
        Map<String, String> params,
        Map<String, String> headers,
        Object body,
        Class<T> responseType
    );

    // HTTP PUT
    <T> HttpResponse<T> put(
        String url,
        Map<String, String> params,
        Map<String, String> headers,
        Object body,
        Class<T> responseType
    );

    // HTTP PATCH
    <T> HttpResponse<T> patch(
        String url,
        Map<String, String> params,
        Map<String, String> headers,
        Object body,
        Class<T> responseType
    );

    // HTTP DELETE
    <T> HttpResponse<T> delete(
        String url,
        Map<String, String> params,
        Map<String, String> headers,
        Class<T> responseType
    );
}
```

### HttpResponse Class

```java
@Value
@Builder
public class HttpResponse<T> {
    int statusCode;           // HTTP status code (0 for network failures)
    Map<String, String> headers; // Response headers
    T body;                   // Deserialized response body
    boolean success;          // true if statusCode < 400
    String correlationId;     // Request correlation ID
}
```

## Configuration

### HTTP Client Configuration

```properties
# =======================================================
# HTTP CLIENT CONFIGURATION
# =======================================================

# Request timeout in milliseconds (default: 5000)
http.client.timeout-millis=10000

# Maximum retry attempts for failed requests (default: 3)
http.client.max-retries=5

# Circuit breaker failure threshold (default: 5)
http.client.circuit-breaker-threshold=10

# Circuit breaker timeout in milliseconds (default: 60000)
http.client.circuit-breaker-timeout-millis=120000

# Proxy configuration
http.client.proxy-host=proxy.company.com
http.client.proxy-port=8080
http.client.proxy-domains=*.internal.company.com,api.service.com
```

### TLS Configuration

```properties
# =======================================================
# TLS CONFIGURATION
# =======================================================

# Enable/disable TLS validation (default: true)
http.tls.enabled=true

# Domains that should skip SSL verification (comma-separated)
# Default: localhost,127.0.0.1
http.tls.insecure-domains=localhost,127.0.0.1,test.example.com

# Path to trust store file (relative to resources folder)
# Default: certs/truststore.jks
http.tls.trust-store-path=certs/custom-truststore.jks

# Trust store password (default: changeit)
http.tls.trust-store-password=mypassword
```

### PII Masking Configuration

```properties
# =======================================================
# PII MASKING CONFIGURATION
# =======================================================

# Mask value for sensitive data (default: ***)
http.pii.mask=****

# Header PII classification (values: NONE, LOW, MEDIUM, HIGH, SECRET)
http.pii.headers.authorization=SECRET
http.pii.headers.cookie=SECRET
http.pii.headers.x-api-key=SECRET
http.pii.headers.x-auth-token=HIGH

# JSON Body PII classification
http.pii.json.password=SECRET
http.pii.json.token=HIGH
http.pii.json.accessToken=HIGH
http.pii.json.refreshToken=HIGH
http.pii.json.email=MEDIUM
http.pii.json.phone=MEDIUM
http.pii.json.ssn=SECRET
http.pii.json.creditCard=HIGH

# Custom field names can be mapped to PII levels
http.pii.json.apiKey=HIGH
http.pii.json.sessionId=MEDIUM
```

### Audit Configuration

```properties
# =======================================================
# AUDIT CONFIGURATION
# =======================================================

# Enable/disable audit logging (default: false)
http.audit.enabled=true

# Enable GDPR compliance audit (default: false)
http.audit.gdpr-enabled=true

# Enable PCI compliance audit (default: false)
http.audit.pci-enabled=true

# Service name for audit events (default: unknown-service)
http.audit.service-name=my-microservice
```

### Correlation ID Configuration

```properties
# =======================================================
# CORRELATION ID CONFIGURATION
# =======================================================

# Header name for correlation ID (default: X-Correlation-Id)
http.correlation-id.header=X-Correlation-Id
```

### Logging Configuration

```properties
# =======================================================
# LOGGING CONFIGURATION
# =======================================================

# Default log level for HTTP utils (default: INFO)
logging.level.com.example.http=INFO

# Enable TRACE logging for detailed request/response logging
# WARNING: This will log masked headers and bodies
logging.level.com.example.http=TRACE
```

## Features

### Resilience Patterns

- **Automatic Retry**: Configurable retry attempts with exponential backoff
- **Circuit Breaker**: Prevents cascading failures with configurable thresholds
- **Timeout Protection**: Configurable request timeouts to prevent hanging requests

### Security & PII Protection

- **Header Masking**: Automatically masks sensitive headers (Authorization, API keys, etc.)
- **JSON Body Masking**: Detects and masks sensitive fields in JSON request/response bodies
- **Regex-based Detection**: Built-in detection for credit card numbers and SSN patterns
- **Configurable Masking**: Custom masking strings and field classifications

### Observability

- **Correlation ID**: Automatic propagation of correlation IDs across service calls
- **Structured Logging**: Consistent log format with correlation IDs
- **Audit Trail**: GDPR and PCI compliant audit logging
- **OpenTelemetry Integration**: Compatible with distributed tracing systems

### TLS/SSL Support

- **Custom Trust Stores**: Support for custom trust store files
- **Insecure Domains**: Configurable list of domains to skip SSL verification
- **Flexible Configuration**: Enable/disable TLS validation per environment

## Logging

The extension provides multi-level logging with automatic PII masking:

### Log Levels

- **INFO**: Minimal logging - only errors and important events
- **DEBUG**: Request/response metadata without bodies
- **TRACE**: Full request/response details with PII-masked headers and bodies

### Log Format

```
[FINEST] HTTP GET https://api.example.com/users headers={authorization=****, content-type=application/json} body={"username":"john","password":"****"} piiLevel=SECRET cid=abc-123-def
[FINEST] HTTP RESPONSE status=200 headers={content-type=application/json} body={"userId":123,"email":"****"} piiLevel=MEDIUM cid=abc-123-def attempt=1
```

### Audit Logging

When audit is enabled, additional audit events are logged:

```
[AUDIT] GDPR: HTTP_CLIENT_CALL service=my-service operation=GET url=https://api.example.com/users status=200 piiLevel=MEDIUM cid=abc-123-def
```

## Security & Compliance

### PII Classification Levels

- **NONE**: Safe data (correlation IDs, request IDs)
- **LOW**: Basic identifiers
- **MEDIUM**: Email addresses, phone numbers
- **HIGH**: API tokens, session tokens
- **SECRET**: Passwords, private keys, secrets

### Audit Categories

- **GDPR**: General Data Protection Regulation compliance
- **PCI**: Payment Card Industry compliance

### Safe Defaults

- TLS validation enabled by default
- PII masking enabled with conservative defaults
- Audit logging disabled by default (opt-in)
- Circuit breaker and retry with reasonable defaults

## Use Cases

### Authentication Libraries

```java
public class AuthService {

    @Inject
    HttpClient http;

    public TokenResponse authenticate(String username, String password) {
        HttpResponse<TokenResponse> response = http.post(
            authEndpoint,
            Map.of(),
            Map.of("Content-Type", "application/json"),
            new AuthRequest(username, password),
            TokenResponse.class
        );

        // Password automatically masked in logs
        return response.getBody();
    }
}
```

### API Gateways

```java
public class GatewayService {

    @Inject
    HttpClient http;

    public GatewayResponse proxy(Request request) {
        // Correlation ID automatically propagated
        HttpResponse<byte[]> response = http.post(
            backendUrl,
            request.getParams(),
            request.getHeaders(),
            request.getBody(),
            byte[].class
        );

        return new GatewayResponse(response);
    }
}
```

### Platform SDKs

```java
public class PlatformClient {

    @Inject
    HttpClient http;

    public List<Order> getOrders(String apiKey) {
        HttpResponse<OrderList> response = http.get(
            "/api/orders",
            Map.of("limit", "100"),
            Map.of("X-API-Key", apiKey), // Automatically masked
            OrderList.class
        );

        return response.getBody().getOrders();
    }
}
```

## Building & Testing

### Prerequisites

- Java 17+
- Maven 3.8+

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the extension
mvn clean package

# Install to local repository
mvn clean install

# Deploy to remote repository
mvn clean deploy
```

### Testing

The extension includes comprehensive tests using:

- **JUnit 5**: Unit tests for all components
- **WireMock**: HTTP endpoint mocking for integration tests
- **QuarkusTest**: CDI integration testing

Run tests with:
```bash
mvn test
```

### Code Coverage

Minimum code coverage requirement: 80%

```bash
mvn clean test jacoco:report
```

## Author

**Priyanshu Sharan** - [LinkedIn](https://www.linkedin.com/in/priyanshu-sharan/) | [GitHub](https://github.com/priyanshu253)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

### Development Setup

```bash
git clone https://github.com/hexeditors/quarkus-http.git
cd quarkus-http
mvn clean install
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

package com.example.orderservice.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class OrderAggregationService {

    private final RestTemplate restTemplate;
    private final MeterRegistry meterRegistry;
    private final String userServiceUrl;
    private final String userServiceFallbackUrl;
    private final String productServiceUrl;
    private final String productServiceFallbackUrl;
    private final String paymentServiceUrl;
    private final String paymentServiceFallbackUrl;
    private final String notificationServiceUrl;
    private final String notificationServiceFallbackUrl;
    private final String analyticsServiceUrl;
    private final String analyticsServiceFallbackUrl;

    public OrderAggregationService(
            RestTemplate restTemplate,
            MeterRegistry meterRegistry,
            @Value("${services.user.primary-url}") String userServiceUrl,
            @Value("${services.user.fallback-url}") String userServiceFallbackUrl,
            @Value("${services.product.primary-url}") String productServiceUrl,
            @Value("${services.product.fallback-url}") String productServiceFallbackUrl,
            @Value("${services.payment.primary-url}") String paymentServiceUrl,
            @Value("${services.payment.fallback-url}") String paymentServiceFallbackUrl,
            @Value("${services.notification.primary-url}") String notificationServiceUrl,
            @Value("${services.notification.fallback-url}") String notificationServiceFallbackUrl,
            @Value("${services.analytics.primary-url}") String analyticsServiceUrl,
            @Value("${services.analytics.fallback-url}") String analyticsServiceFallbackUrl) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
        this.userServiceUrl = userServiceUrl;
        this.userServiceFallbackUrl = userServiceFallbackUrl;
        this.productServiceUrl = productServiceUrl;
        this.productServiceFallbackUrl = productServiceFallbackUrl;
        this.paymentServiceUrl = paymentServiceUrl;
        this.paymentServiceFallbackUrl = paymentServiceFallbackUrl;
        this.notificationServiceUrl = notificationServiceUrl;
        this.notificationServiceFallbackUrl = notificationServiceFallbackUrl;
        this.analyticsServiceUrl = analyticsServiceUrl;
        this.analyticsServiceFallbackUrl = analyticsServiceFallbackUrl;
    }

    public Map<String, Object> placeOrder() {
        return Timer.builder("orders_duration_seconds")
                .description("Time taken to place an order across all services")
                .register(meterRegistry)
                .record(() -> {
                    try {
                        Map<String, Object> response = new LinkedHashMap<>();
                        response.put("user", getStringWithFallback("user-service", userServiceUrl, userServiceFallbackUrl));
                        response.put("product", getStringWithFallback("product-service", productServiceUrl, productServiceFallbackUrl));
                        response.put("payment", postWithFallback("payment-service", paymentServiceUrl, paymentServiceFallbackUrl));
                        response.put("notification", postWithFallback("notification-service", notificationServiceUrl, notificationServiceFallbackUrl));
                        response.put("analytics", postWithFallback("analytics-service", analyticsServiceUrl, analyticsServiceFallbackUrl));
                        response.put("message", "Order placed successfully");
                        incrementOrderCounter("success");
                        return response;
                    } catch (RuntimeException exception) {
                        incrementOrderCounter("failure");
                        throw exception;
                    }
                });
    }

    private String getStringWithFallback(String serviceName, String primaryUrl, String fallbackUrl) {
        try {
            String response = restTemplate.getForObject(primaryUrl, String.class);
            incrementServiceCallCounter(serviceName, "GET", "primary", "success");
            return response;
        } catch (RestClientException exception) {
            incrementServiceCallCounter(serviceName, "GET", "primary", "failure");
            String response = restTemplate.getForObject(fallbackUrl, String.class);
            incrementServiceCallCounter(serviceName, "GET", "fallback", "success");
            return response;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postWithFallback(String serviceName, String primaryUrl, String fallbackUrl) {
        try {
            Map<String, Object> response = restTemplate.postForObject(primaryUrl, HttpEntity.EMPTY, Map.class);
            incrementServiceCallCounter(serviceName, "POST", "primary", "success");
            return response;
        } catch (RestClientException exception) {
            incrementServiceCallCounter(serviceName, "POST", "primary", "failure");
            Map<String, Object> response = restTemplate.postForObject(fallbackUrl, HttpEntity.EMPTY, Map.class);
            incrementServiceCallCounter(serviceName, "POST", "fallback", "success");
            return response;
        }
    }

    private void incrementOrderCounter(String result) {
        Counter.builder("orders_total")
                .description("Total order requests handled by order-service")
                .tag("result", result)
                .register(meterRegistry)
                .increment();
    }

    private void incrementServiceCallCounter(String serviceName, String method, String source, String result) {
        Counter.builder("downstream_service_calls_total")
                .description("Total downstream calls made while placing orders")
                .tag("service", serviceName)
                .tag("method", method)
                .tag("source", source)
                .tag("result", result)
                .register(meterRegistry)
                .increment();
    }
}

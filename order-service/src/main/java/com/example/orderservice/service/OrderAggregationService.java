package com.example.orderservice.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderAggregationService {

    private final RestTemplate restTemplate;
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
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user", getStringWithFallback(userServiceUrl, userServiceFallbackUrl));
        response.put("product", getStringWithFallback(productServiceUrl, productServiceFallbackUrl));
        response.put("payment", postWithFallback(paymentServiceUrl, paymentServiceFallbackUrl));
        response.put("notification", postWithFallback(notificationServiceUrl, notificationServiceFallbackUrl));
        response.put("analytics", postWithFallback(analyticsServiceUrl, analyticsServiceFallbackUrl));
        response.put("message", "Order placed successfully");
        return response;
    }

    private String getStringWithFallback(String primaryUrl, String fallbackUrl) {
        try {
            return restTemplate.getForObject(primaryUrl, String.class);
        } catch (RestClientException exception) {
            return restTemplate.getForObject(fallbackUrl, String.class);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postWithFallback(String primaryUrl, String fallbackUrl) {
        try {
            return restTemplate.postForObject(primaryUrl, HttpEntity.EMPTY, Map.class);
        } catch (RestClientException exception) {
            return restTemplate.postForObject(fallbackUrl, HttpEntity.EMPTY, Map.class);
        }
    }
}
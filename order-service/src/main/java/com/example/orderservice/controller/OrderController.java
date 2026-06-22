package com.example.orderservice.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderservice.service.OrderAggregationService;

@RestController
public class OrderController {

    private final OrderAggregationService orderAggregationService;

    public OrderController(OrderAggregationService orderAggregationService) {
        this.orderAggregationService = orderAggregationService;
    }

    @PostMapping("/order")
    public Map<String, Object> placeOrder() {
        return orderAggregationService.placeOrder();
    }
}
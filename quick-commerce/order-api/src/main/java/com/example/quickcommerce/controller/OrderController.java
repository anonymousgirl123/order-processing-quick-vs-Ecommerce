package com.example.quickcommerce.controller;

import com.example.quickcommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> placeOrder(
            @RequestParam String storeId,
            @RequestBody Map<String, Integer> items
    ) {
        service.placeOrder(storeId, items);
        return ResponseEntity.accepted().build();
    }
}

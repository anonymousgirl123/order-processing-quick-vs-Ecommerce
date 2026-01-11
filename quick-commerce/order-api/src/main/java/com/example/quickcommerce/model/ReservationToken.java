package com.example.quickcommerce.model;

import java.time.Instant;
import java.util.Map;

public record ReservationToken(
        String reservationId,
        String orderId,
        String storeId,
        Map<String, Integer> items,
        Instant expiresAt
) {}

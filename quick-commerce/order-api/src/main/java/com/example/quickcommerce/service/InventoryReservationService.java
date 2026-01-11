package com.example.quickcommerce.service;

import com.example.quickcommerce.model.ReservationToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class InventoryReservationService {

    private static final Duration TTL = Duration.ofMinutes(3);

    private final StringRedisTemplate redis;

    public InventoryReservationService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public ReservationToken reserve(
            String orderId,
            String storeId,
            Map<String, Integer> items
    ) {

        // 1. Atomically decrement inventory
        items.forEach((itemId, qty) -> {
            String key = inventoryKey(storeId, itemId);
            Long remaining = redis.opsForValue().decrement(key, qty);

            if (remaining == null || remaining < 0) {
                rollback(storeId, items);
                throw new RuntimeException("Out of stock: " + itemId);
            }
        });

        // 2. Create reservation token
        String reservationId = UUID.randomUUID().toString();
        ReservationToken token = new ReservationToken(
                reservationId,
                orderId,
                storeId,
                items,
                Instant.now().plus(TTL)
        );

        // 3. Store token with TTL
        redis.opsForValue().set(
                reservationKey(reservationId),
                token.toString(),
                TTL
        );

        return token;
    }

    public void release(ReservationToken token) {
        token.items().forEach((itemId, qty) ->
                redis.opsForValue().increment(
                        inventoryKey(token.storeId(), itemId), qty
                )
        );
        redis.delete(reservationKey(token.reservationId()));
    }

    private void rollback(String storeId, Map<String, Integer> items) {
        items.forEach((itemId, qty) ->
                redis.opsForValue().increment(
                        inventoryKey(storeId, itemId), qty
                )
        );
    }

    private String inventoryKey(String storeId, String itemId) {
        return "inventory:" + storeId + ":" + itemId;
    }

    private String reservationKey(String reservationId) {
        return "reservation:" + reservationId;
    }
}

package com.example.quickcommerce.service;

import com.example.quickcommerce.model.Order;
import com.example.quickcommerce.model.ReservationToken;
import com.example.quickcommerce.repo.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final InventoryReservationService reservationService;
    private final OrderRepository orderRepo;
    private final KafkaTemplate<String, Object> kafka;

    public OrderService(
            InventoryReservationService reservationService,
            OrderRepository orderRepo,
            KafkaTemplate<String, Object> kafka
    ) {
        this.reservationService = reservationService;
        this.orderRepo = orderRepo;
        this.kafka = kafka;
    }

    @Transactional
    public void placeOrder(String storeId, Map<String, Integer> items) {

        String orderId = UUID.randomUUID().toString();

        ReservationToken token =
                reservationService.reserve(orderId, storeId, items);

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStoreId(storeId);
        order.setReservationId(token.reservationId());
        order.setStatus("CREATED");
        order.setCreatedAt(Instant.now());
        order.setExpiresAt(token.expiresAt());

        orderRepo.save(order);

        // Kafka AFTER correctness
        kafka.send("order-events", orderId, token);
    }
}

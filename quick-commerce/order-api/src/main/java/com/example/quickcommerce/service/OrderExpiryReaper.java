package com.example.quickcommerce.service;

import com.example.quickcommerce.model.Order;
import com.example.quickcommerce.repo.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OrderExpiryReaper {

    private final OrderRepository orderRepo;

    public OrderExpiryReaper(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Scheduled(fixedDelay = 5000)
    public void expireOrders() {
        List<Order> expired =
                orderRepo.findByStatusAndExpiresAtBefore(
                        "CREATED", Instant.now()
                );

        expired.forEach(order -> {
            order.setStatus("EXPIRED");
            orderRepo.save(order);
        });
    }
}

package com.example.quickcommerce.repo;

import com.example.quickcommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByStatusAndExpiresAtBefore(
            String status,
            Instant time
    );
}

package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import com.goldenflame.pg102.model.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    long countByOrderStatusIn(List<String> statuses); // To count active orders
    List<Order> findByOrderStatusIn(List<String> statuses); // Find all orders with a given status
    List<Order> findByUserOrderByOrderDateDesc(User user);
}

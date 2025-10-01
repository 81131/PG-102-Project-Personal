package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Order;
import com.goldenflame.pg102.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    long countByOrderStatusIn(List<String> statuses);
    List<Order> findByOrderStatusIn(List<String> statuses);

    List<Order> findByUserOrderByOrderDateDesc(User user);

    List<Order> findByDeliveryPersonAndOrderStatus(User deliveryPerson, String status);

}


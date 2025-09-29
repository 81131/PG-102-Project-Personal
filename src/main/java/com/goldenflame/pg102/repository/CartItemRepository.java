package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
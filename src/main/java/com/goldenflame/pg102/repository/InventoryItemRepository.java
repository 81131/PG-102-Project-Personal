package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}
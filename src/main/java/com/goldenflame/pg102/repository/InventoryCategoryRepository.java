package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.InventoryCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, Long> {
}
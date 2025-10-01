package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.InventoryPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; // Add this import
import java.util.List;      // Add this import

public interface InventoryPurchaseRepository extends JpaRepository<InventoryPurchase, Long> {
    // Add this method to find purchases expiring within a date range
    List<InventoryPurchase> findByExpiryDateBetween(LocalDate start, LocalDate end);
}
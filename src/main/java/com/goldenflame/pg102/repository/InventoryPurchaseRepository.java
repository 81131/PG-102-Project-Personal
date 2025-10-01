package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.InventoryPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface InventoryPurchaseRepository extends JpaRepository<InventoryPurchase, Long> {
    List<InventoryPurchase> findByExpiryDateBetween(LocalDate start, LocalDate end);
    List<InventoryPurchase> findByPurchaseDateBetweenOrderByPurchaseDateDesc(LocalDate startDate, LocalDate endDate);
}
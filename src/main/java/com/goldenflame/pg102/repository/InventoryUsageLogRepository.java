package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.InventoryUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Add this import

public interface InventoryUsageLogRepository extends JpaRepository<InventoryUsageLog, Long> {
    List<InventoryUsageLog> findAllByOrderByUsageDateDesc();
}
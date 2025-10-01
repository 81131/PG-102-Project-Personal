package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; // Import LocalDate
import java.util.List;      // Import List

public interface IncomeRepository extends JpaRepository<Income, Long> {
    // Add this method
    List<Income> findByIncomeDateBetweenOrderByIncomeDateDesc(LocalDate startDate, LocalDate endDate);
}
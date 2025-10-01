package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.ManualExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ManualExpenseRepository extends JpaRepository<ManualExpense, Long> {
    List<ManualExpense> findByExpenseDateBetweenOrderByExpenseDateDesc(LocalDate startDate, LocalDate endDate);
}
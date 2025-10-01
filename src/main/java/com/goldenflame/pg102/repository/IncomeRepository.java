package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByIncomeDateBetweenOrderByIncomeDateDesc(LocalDate startDate, LocalDate endDate);
}
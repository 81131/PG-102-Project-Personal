package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
}
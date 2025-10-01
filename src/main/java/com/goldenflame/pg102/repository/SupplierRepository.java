package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
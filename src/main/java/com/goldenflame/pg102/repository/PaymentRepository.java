package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}


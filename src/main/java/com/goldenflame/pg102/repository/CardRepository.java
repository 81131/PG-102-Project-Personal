package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumberAndCvcAndExpiryMonthAndExpiryYear(String cn, String cvc, int em, int ey);
}
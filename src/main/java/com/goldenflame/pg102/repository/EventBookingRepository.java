package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.EventBooking;
import com.goldenflame.pg102.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventBookingRepository extends JpaRepository<EventBooking, Long> {
    List<EventBooking> findAllByOrderByEventDateTimeDesc();

    List<EventBooking> findByUserOrderByEventDateTimeDesc(User user);

}
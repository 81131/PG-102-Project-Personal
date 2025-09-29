package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Notification;
import com.goldenflame.pg102.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByUserAndIsRead(User user, boolean isRead);
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
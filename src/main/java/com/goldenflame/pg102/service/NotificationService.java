package com.goldenflame.pg102.service;

import com.goldenflame.pg102.model.Notification;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.NotificationRepository;
import com.goldenflame.pg102.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(User user, String message, String link) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void notifyKitchenStaff(String message, String link) {
        List<User> kitchenStaff = userRepository.findByRole_NameIn(List.of("ROLE_KITCHEN_SUPERVISOR", "ROLE_KITCHEN_STAFF"));
        for (User staff : kitchenStaff) {
            createNotification(staff, message, link);
        }
    }

    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Transactional
    public List<Notification> getAndMarkAllAsRead(User user) {
        // 1. Fetch all notifications for the user
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);

        // 2. Mark the unread ones as read
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }

        // 3. Save the changes to the database
        notificationRepository.saveAll(notifications);

        return notifications;
    }
}
package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.UserRepository;
import com.goldenflame.pg102.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class ApiNotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public ApiNotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/notifications/unread-count")
    public Map<String, Long> getUnreadNotificationCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Map.of("count", 0L);
        }
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        long count = notificationService.getUnreadNotificationCount(currentUser);
        return Map.of("count", count);
    }
}
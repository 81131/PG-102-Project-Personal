package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.Notification;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.UserRepository;
import com.goldenflame.pg102.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.goldenflame.pg102.model.Notification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

import java.util.Map;
@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showNotifications(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        List<Notification> notifications = notificationService.getAndMarkAllAsRead(currentUser);
        model.addAttribute("notifications", notifications);
        return "notifications";
    }
}
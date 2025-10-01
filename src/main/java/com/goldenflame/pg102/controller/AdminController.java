package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.goldenflame.pg102.repository.InventoryUsageLogRepository;
import com.goldenflame.pg102.model.Review;
import com.goldenflame.pg102.repository.ReviewRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final InventoryUsageLogRepository inventoryUsageLogRepository;
    private final ReviewRepository reviewRepository;

    public AdminController(UserService userService, InventoryUsageLogRepository inventoryUsageLogRepository, ReviewRepository reviewRepository) {
        this.userService = userService;
        this.inventoryUsageLogRepository = inventoryUsageLogRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/users")
    public String showUserManagementPage(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", userService.findAllRoles());
        return "admin/users";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String addressLine1,
                             @RequestParam(required = false) String addressLine2,
                             @RequestParam String city,
                             @RequestParam String primaryPhoneNo,
                             @RequestParam(required = false) String secondaryPhoneNo,
                             @RequestParam Integer roleId) {
        userService.createUser(username, email, password, firstName, lastName, addressLine1,
                addressLine2, city, primaryPhoneNo, secondaryPhoneNo, roleId);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/inventory-logs")
    public String showInventoryLogs(Model model) {
        model.addAttribute("logs", inventoryUsageLogRepository.findAllByOrderByUsageDateDesc());
        return "admin/inventory-logs";
    }

    @GetMapping("/reviews")
    public String showReviewModerationPage(Model model) {
        model.addAttribute("reviews", reviewRepository.findAllByOrderByReviewDateDesc());
        return "admin/moderate-reviews";
    }

    @PostMapping("/reviews/reply/{id}")
    public String replyToReview(@PathVariable Long id,
                                @RequestParam String managerReply,
                                RedirectAttributes redirectAttributes) {
        Review review = reviewRepository.findById(id).orElseThrow();
        review.setManagerReply(managerReply);
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("success", "Your reply has been posted.");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/comment/status/{id}")
    public String toggleCommentStatus(@PathVariable Long id,
                                      @RequestParam String status,
                                      RedirectAttributes redirectAttributes) {
        Review review = reviewRepository.findById(id).orElseThrow();
        review.setCommentStatus(status);
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("success", "Comment status has been updated.");
        return "redirect:/admin/reviews";
    }
}
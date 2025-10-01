package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.OrderItem;
import com.goldenflame.pg102.model.Review;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.OrderItemRepository;
import com.goldenflame.pg102.repository.ReviewRepository;
import com.goldenflame.pg102.repository.UserRepository;
import com.goldenflame.pg102.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ReviewController(ReviewRepository reviewRepository, OrderItemRepository orderItemRepository, UserRepository userRepository, NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @PostMapping("/submit")
    @Transactional
    public String submitOrUpdateReview(@RequestParam("orderItemId") Long orderItemId,
                                       @RequestParam("score") int score,
                                       @RequestParam(name = "comment", required = false) String comment,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order item ID"));
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Security check: ensure the order item belongs to the current user
        if (!orderItem.getOrder().getUser().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to review this item.");
            return "redirect:/orders/my-history";
        }

        Review review = orderItem.getReview();
        boolean isNewReview = (review == null);

        if (isNewReview) {
            review = new Review();
            review.setOrderItem(orderItem);
            review.setUser(currentUser);
            review.setCatalogueItem(orderItem.getCatalogueItem());
        }

        review.setScore(score);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());
        // If an existing comment was removed by a manager, editing it makes it visible again for re-moderation
        review.setCommentStatus("VISIBLE");

        reviewRepository.save(review);

        // Notify managers if an existing review is edited
        if (!isNewReview) {
            List<User> managers = userRepository.findByRole_Name("ROLE_MANAGER");
            for (User manager : managers) {
                notificationService.createNotification(manager,
                        "A review for '" + review.getCatalogueItem().getName() + "' was updated by a customer.",
                        "/admin/reviews");
            }
        }

        redirectAttributes.addFlashAttribute("success", "Your review has been submitted successfully!");
        return "redirect:/item/" + orderItem.getCatalogueItem().getId() + "/reviews";
    }
}
package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.*;
import com.goldenflame.pg102.repository.*;
import com.goldenflame.pg102.service.CatalogueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;
import java.util.function.Function;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    private final CatalogueService catalogueService;
    private final CategoryRepository categoryRepository;
    private final EventBookingRepository eventBookingRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    public ViewController(CatalogueService catalogueService, CategoryRepository categoryRepository, EventBookingRepository eventBookingRepository, ReviewRepository reviewRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.catalogueService = catalogueService;
        this.categoryRepository = categoryRepository;
        this.eventBookingRepository = eventBookingRepository;
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }


    @GetMapping("/menu")
    public String menu(Model model) {
        List<Category> foodCategories = categoryRepository.findAll().stream()
                .filter(category -> {
                    String lowerCaseName = category.getName().toLowerCase();
                    return !lowerCaseName.contains("event") &&
                            !lowerCaseName.contains("parties") &&
                            !lowerCaseName.startsWith("inventory");
                })
                .collect(Collectors.toList());

        model.addAttribute("categories", foodCategories);
        return "menu";
    }

    @GetMapping("/menu/{categoryName}")
    public String viewCategory(@PathVariable("categoryName") String categoryName, Model model) {
        Optional<Category> categoryOptional = categoryRepository.findByNameIgnoreCase(categoryName.replace("-", " "));
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            model.addAttribute("categoryName", category.getName());
            model.addAttribute("items", catalogueService.getItemsByCategory(category));
            return "category";
        } else {
            return "redirect:/menu";
        }
    }

    @GetMapping("/events")
    public String viewEvents(Model model) {
        List<Category> eventCategories = categoryRepository.findAll().stream()
                .filter(category -> category.getName().toLowerCase().contains("event") || category.getName().toLowerCase().contains("parties"))
                .collect(Collectors.toList());
        model.addAttribute("categories", eventCategories);
        return "events/event-categories";
    }

    @GetMapping("/events/{categoryName}")
    public String viewEventPackages(@PathVariable("categoryName") String categoryName, Model model) {
        return viewCategory(categoryName, model);
    }

    @GetMapping("/item/{id}")
    public String viewItemDetails(@PathVariable("id") Long id, Model model) {
        Optional<CatalogueItem> itemOptional = catalogueService.findById(id);
        if (itemOptional.isPresent()) {
            CatalogueItem item = itemOptional.get();
            model.addAttribute("item", item);

            String categoryName = item.getCategory().getName().toLowerCase();

            // Check if it's an event package
            if (categoryName.contains("event") || categoryName.contains("parties")) {
                List<LocalDate> unavailableDates = eventBookingRepository.findAll().stream()
                        .filter(booking -> !booking.getStatus().equals("CANCELLED") && !booking.getStatus().equals("REJECTED"))
                        .map(booking -> booking.getEventDateTime().toLocalDate())
                        .collect(Collectors.toList());
                model.addAttribute("unavailableDates", unavailableDates);
                return "events/event-details";
            } else {
                return "item-details";
            }
        } else {
            return "redirect:/menu";
        }
    }




    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }


    @GetMapping("/order/success")
    public String orderSuccess() {
        return "order-success";
    }

    @GetMapping("/item/{id}/reviews")
    public String showItemReviews(@PathVariable("id") Long id, Model model) {
        Optional<CatalogueItem> itemOptional = catalogueService.findById(id);
        if (itemOptional.isEmpty()) {
            return "redirect:/menu";
        }
        CatalogueItem item = itemOptional.get();
        model.addAttribute("item", item);

        List<Review> reviews = reviewRepository.findByCatalogueItemIdOrderByReviewDateDesc(id);
        model.addAttribute("reviews", reviews);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<Long, OrderItem> reviewableItems = Map.of();

        if (principal instanceof UserDetails) {
            User currentUser = userRepository.findByUsername(((UserDetails) principal).getUsername()).orElseThrow();
            reviewableItems = orderRepository.findByUserOrderByOrderDateDesc(currentUser).stream()
                    .filter(order -> "COMPLETED".equals(order.getOrderStatus()))
                    .flatMap(order -> order.getOrderItems().stream())
                    .filter(orderItem -> orderItem.getCatalogueItem().getId().equals(id) && orderItem.getReview() == null)
                    .collect(Collectors.toMap(orderItem -> orderItem.getOrder().getId(), Function.identity(), (first, second) -> first));
        }
        model.addAttribute("reviewableItems", reviewableItems);

        return "reviews";
    }
}
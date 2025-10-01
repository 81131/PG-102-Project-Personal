package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.CatalogueItemType;
import com.goldenflame.pg102.model.Category;
import com.goldenflame.pg102.repository.CategoryRepository;
import com.goldenflame.pg102.service.CatalogueService;
import com.goldenflame.pg102.repository.EventBookingRepository; // Import this
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    private final CatalogueService catalogueService;
    private final CategoryRepository categoryRepository;
    private final EventBookingRepository eventBookingRepository; // Inject this


    public ViewController(CatalogueService catalogueService, CategoryRepository categoryRepository, EventBookingRepository eventBookingRepository) {
        this.catalogueService = catalogueService;
        this.categoryRepository = categoryRepository;
        this.eventBookingRepository = eventBookingRepository; // Add this
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
        // Fetch only FOOD categories for the menu page by excluding all other types
        List<Category> foodCategories = categoryRepository.findAll().stream()
                .filter(category -> {
                    String lowerCaseName = category.getName().toLowerCase();
                    return !lowerCaseName.contains("event") &&
                            !lowerCaseName.contains("parties") &&
                            !lowerCaseName.startsWith("inventory"); // <-- ADD THIS LINE
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
        return "events/event-categories"; // Use a new template to show event categories
    }

    @GetMapping("/events/{categoryName}")
    public String viewEventPackages(@PathVariable("categoryName") String categoryName, Model model) {
        return viewCategory(categoryName, model); // Reuse the same logic as the food category page
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
                // Fetch unavailable dates for the calendar
                List<LocalDate> unavailableDates = eventBookingRepository.findAll().stream()
                        .filter(booking -> !booking.getStatus().equals("CANCELLED") && !booking.getStatus().equals("REJECTED"))
                        .map(booking -> booking.getEventDateTime().toLocalDate())
                        .collect(Collectors.toList());
                model.addAttribute("unavailableDates", unavailableDates);
                return "events/event-details"; // Show the special event details page
            } else {
                return "item-details"; // Show the normal food details page
            }
        } else {
            return "redirect:/menu";
        }
    }

    // Add to your ViewController class
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // The name of our new template
    }


    // Add this method to ViewController.java
    @GetMapping("/order/success")
    public String orderSuccess() {
        return "order-success";
    }
}
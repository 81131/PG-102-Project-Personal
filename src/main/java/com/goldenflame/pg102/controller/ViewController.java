package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.CatalogueItemType;
import com.goldenflame.pg102.service.CatalogueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    private final CatalogueService catalogueService;

    public ViewController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
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
        // Get all enum values, convert to a stream, and filter out EVENT_PACKAGE
        List<CatalogueItemType> foodCategories = Arrays.stream(CatalogueItemType.values())
                .filter(category -> category != CatalogueItemType.EVENT_PACKAGE)
                .collect(Collectors.toList());

        model.addAttribute("categories", foodCategories);
        return "menu";
    }

    @GetMapping("/menu/{category}")
    public String viewCategory(@PathVariable("category") String categoryName, Model model) {
        try {
            CatalogueItemType itemType = CatalogueItemType.valueOf(categoryName.toUpperCase());
            model.addAttribute("categoryName", itemType.toString().replace("_", " "));
            model.addAttribute("items", catalogueService.getItemsByType(itemType));
            return "category";
        } catch (IllegalArgumentException e) {
            return "redirect:/menu";
        }
    }

    @GetMapping("/events")
    public String viewEvents(Model model) {
        model.addAttribute("categoryName", "Event Packages");
        model.addAttribute("items", catalogueService.getItemsByType(CatalogueItemType.EVENT_PACKAGE));
        return "category";
    }

    @GetMapping("/item/{id}")
    public String viewItemDetails(@PathVariable("id") Long id, Model model) {
        Optional<CatalogueItem> itemOptional = catalogueService.findById(id);
        if (itemOptional.isPresent()) {
            model.addAttribute("item", itemOptional.get());
            return "item-details"; // This will be our new template
        } else {
            return "redirect:/menu"; // If item not found, redirect to menu
        }
    }

    // Add to your ViewController class
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // The name of our new template
    }


    @GetMapping("/order/success")
    public String orderSuccess() {
        return "order-success";
    }
}
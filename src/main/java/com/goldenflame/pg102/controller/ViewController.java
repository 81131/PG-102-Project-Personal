package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.CatalogueItemType;
import com.goldenflame.pg102.service.CatalogueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        model.addAttribute("categories", CatalogueItemType.values());
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
}
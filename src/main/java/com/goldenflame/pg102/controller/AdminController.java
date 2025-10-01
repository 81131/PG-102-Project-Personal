package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.goldenflame.pg102.repository.InventoryUsageLogRepository;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final InventoryUsageLogRepository inventoryUsageLogRepository; // Inject this

    public AdminController(UserService userService, InventoryUsageLogRepository inventoryUsageLogRepository) { // Update constructor
        this.userService = userService;
        this.inventoryUsageLogRepository = inventoryUsageLogRepository; // Add this
    }

    @GetMapping("/users")
    public String showUserManagementPage(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", userService.findAllRoles());
        return "admin/users"; // Path to the new Thymeleaf template
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

    // ADD THIS NEW METHOD
    @GetMapping("/inventory-logs")
    public String showInventoryLogs(Model model) {
        model.addAttribute("logs", inventoryUsageLogRepository.findAllByOrderByUsageDateDesc());
        return "admin/inventory-logs";
    }
}
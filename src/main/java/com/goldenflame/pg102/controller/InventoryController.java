package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.*;
import com.goldenflame.pg102.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryCategoryRepository inventoryCategoryRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;
    private final InventoryUsageLogRepository inventoryUsageLogRepository;
    private final UserRepository userRepository;

    public InventoryController(InventoryItemRepository inventoryItemRepository,
                               InventoryCategoryRepository inventoryCategoryRepository,
                               SupplierRepository supplierRepository,
                               InventoryPurchaseRepository inventoryPurchaseRepository,
                               InventoryUsageLogRepository inventoryUsageLogRepository,
                               UserRepository userRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryCategoryRepository = inventoryCategoryRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryPurchaseRepository = inventoryPurchaseRepository;
        this.inventoryUsageLogRepository = inventoryUsageLogRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showInventoryDashboard(Model model) {
        model.addAttribute("items", inventoryItemRepository.findAll());
        model.addAttribute("inventoryCategories", inventoryCategoryRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());

        // --- EXPIRY DATE LOGIC ---
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        List<InventoryPurchase> expiringSoon = inventoryPurchaseRepository.findByExpiryDateBetween(today, nextWeek);
        model.addAttribute("expiringSoon", expiringSoon);
        // --- END EXPIRY DATE LOGIC ---

        return "inventory/dashboard";
    }

    @PostMapping("/item/add")
    @Transactional
    public String addNewInventoryItem(@RequestParam String name,
                                      @RequestParam Long categoryId,
                                      @RequestParam(required = false) String newCategoryName, // Add this
                                      @RequestParam String measurementUnit,
                                      @RequestParam float lowStockThreshold,
                                      RedirectAttributes redirectAttributes) {

        InventoryCategory category;
        // --- DYNAMIC CATEGORY LOGIC ---
        if (categoryId == -1 && newCategoryName != null && !newCategoryName.isBlank()) {
            InventoryCategory newCategory = new InventoryCategory();
            newCategory.setName(newCategoryName);
            category = inventoryCategoryRepository.save(newCategory);
        } else {
            category = inventoryCategoryRepository.findById(categoryId).orElseThrow();
        }
        // --- END DYNAMIC CATEGORY LOGIC ---

        InventoryItem newItem = new InventoryItem();
        newItem.setName(name);
        newItem.setCategory(category); // Use the resolved category
        newItem.setMeasurementUnit(measurementUnit);
        newItem.setLowStockThreshold(lowStockThreshold);
        newItem.setCurrentQuantity(0);
        inventoryItemRepository.save(newItem);

        redirectAttributes.addFlashAttribute("success", "New inventory item '" + name + "' added successfully.");
        return "redirect:/inventory";
    }

    @PostMapping("/purchase/add")
    @Transactional
    public String recordPurchase(@RequestParam Long itemId,
                                 @RequestParam Long supplierId,
                                 @RequestParam float quantity,
                                 @RequestParam float unitPrice,
                                 @RequestParam LocalDate purchaseDate,
                                 @RequestParam(required = false) LocalDate expiryDate,
                                 RedirectAttributes redirectAttributes) {
        InventoryItem item = inventoryItemRepository.findById(itemId).orElseThrow();

        InventoryPurchase purchase = new InventoryPurchase();
        purchase.setInventoryItem(item);
        purchase.setSupplier(supplierRepository.findById(supplierId).orElse(null));
        purchase.setQuantityPurchased(quantity);
        purchase.setUnitPrice(unitPrice);
        purchase.setPurchaseDate(purchaseDate);
        purchase.setExpiryDate(expiryDate);
        inventoryPurchaseRepository.save(purchase);

        item.setCurrentQuantity(item.getCurrentQuantity() + quantity);
        inventoryItemRepository.save(item);

        redirectAttributes.addFlashAttribute("success", "Purchase recorded. Stock updated for '" + item.getName() + "'.");
        return "redirect:/inventory";
    }

    @PostMapping("/usage/add")
    @Transactional
    public String recordUsage(@RequestParam Long itemId,
                              @RequestParam float quantity,
                              @RequestParam String reason,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        InventoryItem item = inventoryItemRepository.findById(itemId).orElseThrow();
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        if (item.getCurrentQuantity() < quantity) {
            redirectAttributes.addFlashAttribute("error", "Cannot record usage. Insufficient stock for '" + item.getName() + "'.");
            return "redirect:/inventory";
        }

        // 1. Log the usage
        InventoryUsageLog usageLog = new InventoryUsageLog();
        usageLog.setInventoryItem(item);
        usageLog.setQuantityUsed(quantity);
        usageLog.setReason(reason);
        usageLog.setUser(currentUser);
        usageLog.setUsageDate(LocalDateTime.now()); // <-- THIS IS THE FIX
        inventoryUsageLogRepository.save(usageLog);

        // 2. Update the total stock for the item
        item.setCurrentQuantity(item.getCurrentQuantity() - quantity);
        inventoryItemRepository.save(item);

        redirectAttributes.addFlashAttribute("success", "Stock usage recorded for '" + item.getName() + "'.");
        return "redirect:/inventory";
    }

    @PostMapping("/supplier/add")
    public String addSupplier(@RequestParam String name, @RequestParam String contactInfo, RedirectAttributes redirectAttributes) {
        Supplier newSupplier = new Supplier();
        newSupplier.setName(name);
        newSupplier.setContact_info(contactInfo);
        supplierRepository.save(newSupplier);
        redirectAttributes.addFlashAttribute("success", "New supplier '" + name + "' added.");
        return "redirect:/inventory";
    }
}
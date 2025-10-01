package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.Category;
import com.goldenflame.pg102.repository.CatalogueItemRepository;
import com.goldenflame.pg102.repository.CategoryRepository;
import com.goldenflame.pg102.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/kitchen/catalogue")
public class CatalogueManagementController {

    private final CatalogueItemRepository catalogueItemRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public CatalogueManagementController(CatalogueItemRepository catalogueItemRepository,
                                         CategoryRepository categoryRepository,
                                         FileStorageService fileStorageService) {
        this.catalogueItemRepository = catalogueItemRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String showCatalogueManagementPage(Model model) {
        model.addAttribute("items", catalogueItemRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "kitchen/catalogue";
    }

    @PostMapping("/add")
    @Transactional
    public String addItem(@RequestParam String name,
                          @RequestParam String description,
                          @RequestParam float price,
                          @RequestParam(required = false) Float basePrice,
                          @RequestParam int servingSizePerson,
                          @RequestParam Long categoryId,
                          @RequestParam(required = false) String newCategoryName,
                          @RequestParam(required = false) MultipartFile image,
                          RedirectAttributes redirectAttributes) {

        Category category;
        if (categoryId == -1 && newCategoryName != null && !newCategoryName.isBlank()) {
            // Create a new category if one was specified
            Category newCategory = new Category();
            newCategory.setName(newCategoryName);
            category = categoryRepository.save(newCategory);
        } else {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Category Id:" + categoryId));
        }

        CatalogueItem newItem = new CatalogueItem();
        newItem.setName(name);
        newItem.setDescription(description);
        newItem.setPrice(price);
        newItem.setBasePrice(basePrice);
        newItem.setServingSizePerson(servingSizePerson);
        newItem.setCategory(category);

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(image);
            newItem.setPhotoUrls(List.of(imageUrl));
        }

        catalogueItemRepository.save(newItem);
        redirectAttributes.addFlashAttribute("success", "New item added successfully!");
        return "redirect:/kitchen/catalogue";
    }

    @PostMapping("/edit/{id}")
    public String editItem(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam String description,
                           @RequestParam float price,
                           @RequestParam(required = false) Float basePrice,
                           @RequestParam int servingSizePerson,
                           @RequestParam Long categoryId,
                           @RequestParam(required = false) MultipartFile image,
                           RedirectAttributes redirectAttributes) {

        CatalogueItem item = catalogueItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Item Id:" + id));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category Id:" + categoryId));

        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setBasePrice(basePrice);
        item.setServingSizePerson(servingSizePerson);
        item.setCategory(category);

        if (image != null && !image.isEmpty()) {
            // Delete the old image if it exists
            if (item.getPhotoUrls() != null && !item.getPhotoUrls().isEmpty()) {
                fileStorageService.deleteFile(item.getPhotoUrls().get(0));
            }
            // Store the new image
            String newImageUrl = fileStorageService.storeFile(image);
            item.setPhotoUrls(List.of(newImageUrl));
        }

        catalogueItemRepository.save(item);
        redirectAttributes.addFlashAttribute("success", "Item updated successfully!");
        return "redirect:/kitchen/catalogue";
    }

    @PostMapping("/category/add")
    public String addCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        // Check if category already exists (case-insensitive)
        Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(name);
        if (existingCategory.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Category '" + name + "' already exists.");
        } else {
            Category newCategory = new Category();
            newCategory.setName(name);
            categoryRepository.save(newCategory);
            redirectAttributes.addFlashAttribute("success", "Category '" + name + "' added successfully.");
        }
        return "redirect:/kitchen/catalogue";
    }
}
package com.goldenflame.pg102.service;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.Category; // Import Category
import com.goldenflame.pg102.repository.CatalogueItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogueService {

    private final CatalogueItemRepository catalogueItemRepository;

    public CatalogueService(CatalogueItemRepository catalogueItemRepository) {
        this.catalogueItemRepository = catalogueItemRepository;
    }

    public List<CatalogueItem> getAllItems() {
        return catalogueItemRepository.findAll();
    }

    // Update this method to accept a Category object
    public List<CatalogueItem> getItemsByCategory(Category category) {
        return catalogueItemRepository.findByCategory(category);
    }

    public Optional<CatalogueItem> findById(Long id) {
        return catalogueItemRepository.findById(id);
    }
}
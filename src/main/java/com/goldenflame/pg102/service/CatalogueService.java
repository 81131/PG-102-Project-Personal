package com.goldenflame.pg102.service;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.CatalogueItemType;
import com.goldenflame.pg102.repository.CatalogueItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CatalogueService {

    private final CatalogueItemRepository catalogueItemRepository;

    public CatalogueService(CatalogueItemRepository catalogueItemRepository) {
        this.catalogueItemRepository = catalogueItemRepository;
    }

    public List<CatalogueItem> getAllItems() {
        return catalogueItemRepository.findAll();
    }

    public List<CatalogueItem> getItemsByType(CatalogueItemType itemType) {
        return catalogueItemRepository.findByItemType(itemType);
    }
}
package com.goldenflame.pg102.service;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.CatalogueItemType;
import com.goldenflame.pg102.model.Review;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogueService {

    // This hardcoded list will be replaced by a database repository call later
    private final List<CatalogueItem> catalogue = new ArrayList<>();

    public CatalogueService() {
        // Initialize with sample data
        catalogue.add(new CatalogueItem("Bruschetta", "Toasted bread with fresh tomatoes.", List.of("/images/bruschetta.jpg"), 1, CatalogueItemType.APPETIZER, 10.00f,
                List.of(new Review(5, "Amazing!"), new Review(4))));
        catalogue.add(new CatalogueItem("Stuffed Mushrooms", "Mushroom caps filled with cheese.", List.of("/images/mushrooms.jpg"), 2, CatalogueItemType.APPETIZER, 12.50f,
                List.of(new Review(5), new Review(5), new Review(4, "A bit salty"))));
        catalogue.add(new CatalogueItem("Birthday Party Package", "A complete package for a birthday celebration.", List.of("/images/birthday.jpg"), 50, CatalogueItemType.EVENT_PACKAGE, 500.00f,
                List.of(new Review(5, "Perfect for our celebration."))));
    }

    public List<CatalogueItem> getAllItems() {
        return catalogue;
    }

    public List<CatalogueItem> getItemsByType(CatalogueItemType itemType) {
        return catalogue.stream()
                .filter(item -> item.getItemType() == itemType)
                .collect(Collectors.toList());
    }
}
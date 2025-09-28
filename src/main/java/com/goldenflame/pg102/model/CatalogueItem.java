package com.goldenflame.pg102.model;

import java.util.List;
import java.util.OptionalDouble;

public class CatalogueItem {
    private String name;
    private String description;
    private List<String> photoUrls;
    private int servingSizePerson;
    private CatalogueItemType itemType;
    private float price; // Changed to float
    private List<Review> reviews; // Changed to a list of Review objects

    public CatalogueItem(String name, String description, List<String> photoUrls, int servingSizePerson, CatalogueItemType itemType, float price, List<Review> reviews) {
        this.name = name;
        this.description = description;
        this.photoUrls = photoUrls;
        this.servingSizePerson = servingSizePerson;
        this.itemType = itemType;
        this.price = price;
        this.reviews = reviews;
    }

    // New method to calculate the average rating
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        OptionalDouble average = reviews.stream()
                .mapToInt(Review::getScore)
                .average();
        return average.orElse(0.0);
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getPhotoUrls() { return photoUrls; }
    public int getServingSizePerson() { return servingSizePerson; }
    public CatalogueItemType getItemType() { return itemType; }
    public float getPrice() { return price; }
    public List<Review> getReviews() { return reviews; }
}
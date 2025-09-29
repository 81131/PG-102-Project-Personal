package com.goldenflame.pg102.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "catalogue_items")


public class CatalogueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // This tells JPA to store the list of photo URLs in a separate table
    @ElementCollection
    @CollectionTable(name = "catalogue_item_photos", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls;

    private int servingSizePerson;

    @Enumerated(EnumType.STRING) // Stores the enum name (e.g., "APPETIZER") in the DB
    private CatalogueItemType itemType;

    private float price;

    // One item can have many reviews. 'cascade = CascadeType.ALL' means if we delete an item, its reviews are also deleted.
    @OneToMany(mappedBy = "catalogueItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    // --- Methods and Getters/Setters ---

    // Default constructor for JPA
    public CatalogueItem() {}

    // You can keep your other constructor for creating new items
    public CatalogueItem(String name, String description, List<String> photoUrls, int servingSizePerson, CatalogueItemType itemType, float price, List<Review> reviews) {
        this.name = name;
        this.description = description;
        this.photoUrls = photoUrls;
        this.servingSizePerson = servingSizePerson;
        this.itemType = itemType;
        this.price = price;
        this.reviews = reviews;
    }

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
    }

    // Getters and Setters for all fields...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
    public int getServingSizePerson() { return servingSizePerson; }
    public void setServingSizePerson(int servingSizePerson) { this.servingSizePerson = servingSizePerson; }
    public CatalogueItemType getItemType() { return itemType; }
    public void setItemType(CatalogueItemType itemType) { this.itemType = itemType; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
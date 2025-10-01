package com.goldenflame.pg102.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "catalogue_items")
public class CatalogueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Float basePrice;


    @ElementCollection
    @CollectionTable(name = "catalogue_item_photos", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls;

    private int servingSizePerson;

    private float price;

    @OneToMany(mappedBy = "catalogueItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;



    public CatalogueItem() {}

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
    }

    // Getters and Setters
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
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public Float getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Float basePrice) {
        this.basePrice = basePrice;
    }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
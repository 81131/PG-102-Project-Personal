package com.goldenflame.pg102.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews") // Specifies the table name
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score; // e.g., 5 for 5 stars

    @Column(nullable = true) // The comment can be null
    private String comment;

    // This creates the link back to the CatalogueItem
    @ManyToOne
    @JoinColumn(name = "catalogue_item_id")
    private CatalogueItem catalogueItem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Default constructor for JPA
    public Review() {}

    public Review(int score, String comment) {
        this.score = score;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public CatalogueItem getCatalogueItem() { return catalogueItem; }
    public void setCatalogueItem(CatalogueItem catalogueItem) { this.catalogueItem = catalogueItem; }
}
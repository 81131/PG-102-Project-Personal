package com.goldenflame.pg102.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This links the review to a specific item purchased in an order
    @OneToOne
    @JoinColumn(name = "order_item_id", nullable = false, unique = true)
    private OrderItem orderItem;

    // This is a direct link to the catalogue item for easier querying
    @ManyToOne
    @JoinColumn(name = "catalogue_item_id", nullable = false)
    private CatalogueItem catalogueItem;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int score;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String comment;

    // New field for the manager's public reply
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String managerReply;

    // New field to control comment visibility
    @Column(nullable = false)
    private String commentStatus = "VISIBLE"; // Default to VISIBLE

    @Column(nullable = false)
    private LocalDateTime reviewDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrderItem getOrderItem() { return orderItem; }
    public void setOrderItem(OrderItem orderItem) { this.orderItem = orderItem; }
    public CatalogueItem getCatalogueItem() { return catalogueItem; }
    public void setCatalogueItem(CatalogueItem catalogueItem) { this.catalogueItem = catalogueItem; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getManagerReply() { return managerReply; }
    public void setManagerReply(String managerReply) { this.managerReply = managerReply; }
    public String getCommentStatus() { return commentStatus; }
    public void setCommentStatus(String commentStatus) { this.commentStatus = commentStatus; }
    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
}
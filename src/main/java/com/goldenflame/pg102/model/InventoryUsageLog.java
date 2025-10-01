package com.goldenflame.pg102.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_usage_log")
public class InventoryUsageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    private float quantityUsed;
    private LocalDateTime usageDate;
    private String reason;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public InventoryItem getInventoryItem() { return inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }
    public float getQuantityUsed() { return quantityUsed; }
    public void setQuantityUsed(float quantityUsed) { this.quantityUsed = quantityUsed; }
    public LocalDateTime getUsageDate() { return usageDate; }
    public void setUsageDate(LocalDateTime usageDate) { this.usageDate = usageDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
package com.goldenflame.pg102.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_purchases")
public class InventoryPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private float quantityPurchased;
    private float unitPrice;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public InventoryItem getInventoryItem() { return inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public float getQuantityPurchased() { return quantityPurchased; }
    public void setQuantityPurchased(float quantityPurchased) { this.quantityPurchased = quantityPurchased; }
    public float getUnitPrice() { return unitPrice; }
    public void setUnitPrice(float unitPrice) { this.unitPrice = unitPrice; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}
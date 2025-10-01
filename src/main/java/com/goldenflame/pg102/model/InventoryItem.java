package com.goldenflame.pg102.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private InventoryCategory category;

    private String measurementUnit;
    private float lowStockThreshold;
    private float currentQuantity;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public InventoryCategory getCategory() { return category; }
    public void setCategory(InventoryCategory category) { this.category = category; }
    public String getMeasurementUnit() { return measurementUnit; }
    public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }
    public float getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(float lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    public float getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(float currentQuantity) { this.currentQuantity = currentQuantity; }
}
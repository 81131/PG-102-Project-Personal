package com.goldenflame.pg102.model;

import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "cart_items")
public class CartItem {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

    @ManyToOne
    @JoinColumn(name = "catalogue_item_id", nullable = false)
    private CatalogueItem catalogueItem;

    @Column(nullable = false)
    private int quantity;


    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ShoppingCart getCart() { return cart; }
    public void setCart(ShoppingCart cart) { this.cart = cart; }
    public CatalogueItem getCatalogueItem() { return catalogueItem; }
    public void setCatalogueItem(CatalogueItem catalogueItem) { this.catalogueItem = catalogueItem; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
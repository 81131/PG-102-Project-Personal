package com.goldenflame.pg102.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name = "order_items")
public class OrderItem {

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Review review;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "order_id") private Order order;
    @ManyToOne @JoinColumn(name = "catalogue_item_id") private CatalogueItem catalogueItem;
    private int quantity;
    private float pricePerItem;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public CatalogueItem getCatalogueItem() {
        return catalogueItem;
    }

    public void setCatalogueItem(CatalogueItem catalogueItem) {
        this.catalogueItem = catalogueItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(float pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
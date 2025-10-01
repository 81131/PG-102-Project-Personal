package com.goldenflame.pg102.model;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String contact_info;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact_info() { return contact_info; }
    public void setContact_info(String contact_info) { this.contact_info = contact_info; }
}
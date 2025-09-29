package com.goldenflame.pg102.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String addressLine1;

    @Column(nullable = true) // Can be null
    private String addressLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String primaryPhoneNo;

    @Column(nullable = true) // Can be null
    private String secondaryPhoneNo;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Getters and Setters for all fields...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPrimaryPhoneNo() { return primaryPhoneNo; }
    public void setPrimaryPhoneNo(String primaryPhoneNo) { this.primaryPhoneNo = primaryPhoneNo; }
    public String getSecondaryPhoneNo() { return secondaryPhoneNo; }
    public void setSecondaryPhoneNo(String secondaryPhoneNo) { this.secondaryPhoneNo = secondaryPhoneNo; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
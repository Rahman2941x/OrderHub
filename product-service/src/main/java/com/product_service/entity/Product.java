package com.product_service.entity;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "product_category", nullable = false)
    private String category;
    private String description;
    private Long productOwnerId;
    private BigInteger price;
    private Boolean active;
    private Integer inStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(String productName, String category, String description, Long productOwnerId, BigInteger price, Integer inStock) {
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.productOwnerId = productOwnerId;
        this.price = price;
        this.inStock = inStock;
    }

    public Product(LocalDateTime updatedAt, LocalDateTime createdAt, Integer inStock, Boolean active, BigInteger price, Long productOwnerId, String description, String category, String productName, Long id) {
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.inStock = inStock;
        this.active = active;
        this.price = price;
        this.productOwnerId = productOwnerId;
        this.description = description;
        this.category = category;
        this.productName = productName;
        this.id = id;
    }

    public Product() {
    }

    public Product(String s, String category, String description, BigInteger price, Integer integer) {
    }

    @PrePersist
    private void preUpdate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void postUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProductOwnerId() {
        return productOwnerId;
    }

    public void setProductOwnerId(Long productOwnerId) {
        this.productOwnerId = productOwnerId;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", productOwnerId='" + productOwnerId + '\'' +
                ", price=" + price +
                ", active=" + active +
                ", inStock=" + inStock +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

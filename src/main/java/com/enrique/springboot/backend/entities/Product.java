package com.enrique.springboot.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "description")
    private String description;

    @Column(nullable = false, name = "price")
    private Long price;

    @Column(nullable = false, name = "color")
    private String color;

    @Column(nullable = false, name = "stock")
    private Long stock;

    @Column(name = "image_url")
    private String imageUrl;

    // Borrado lógico
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "desactivation_reason")
    private String desactivationReason;

    @ManyToOne
    @JoinColumn(name = "desactivated_by")
    private User desactivatedBy;

    @Column(name = "desactivation_date")
    private LocalDateTime desactivationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getDesactivationReason() { return desactivationReason; }
    public void setDesactivationReason(String desactivationReason) { this.desactivationReason = desactivationReason; }

    public User getDesactivatedBy() { return desactivatedBy; }
    public void setDesactivatedBy(User desactivatedBy) { this.desactivatedBy = desactivatedBy; }

    public LocalDateTime getDesactivationDate() { return desactivationDate; }
    public void setDesactivationDate(LocalDateTime desactivationDate) { this.desactivationDate = desactivationDate; }
}

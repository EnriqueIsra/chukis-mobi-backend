package com.enrique.springboot.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: muchas imágenes pertenecen a un producto
    // LAZY = no carga las imágenes automáticamente al consultar el producto, solo cuando se accede explícitamente a ellas (mejor rendimiento)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // URL de la imagen (misma estructura que product.imageUrl
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    // Orden de visualización en la galería (o = primera, 1 = segunda, etc.)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    public ProductImage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}

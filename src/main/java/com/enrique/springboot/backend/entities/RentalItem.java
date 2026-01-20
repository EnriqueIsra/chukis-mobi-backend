package com.enrique.springboot.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "rental_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rental_id", "product_id"})
        })
public class RentalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Renta
    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    // Producto
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Cantidad rentada
    @Column(nullable = false)
    private Integer quantity;

    // Precio unitario al momento de la renta
    @Column(nullable = false)
    private Long price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}

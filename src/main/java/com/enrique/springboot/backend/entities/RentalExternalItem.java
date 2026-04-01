package com.enrique.springboot.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_external_items")
public class RentalExternalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A qué item de renta pertenece (producto + renta)
    @ManyToOne
    @JoinColumn(name = "rental_item_id", nullable = false)
    private RentalItem rentalItem;

    // Quién lo proveyó
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    // Cantidad asignada a este proveedor
    @Column(nullable = false)
    private Integer quantity;

    // Costo por unidad que cobra el proveedor
    @Column(name = "unit_cost", nullable = false)
    private Long unitCost;

    // Calculado: quantity * unitCost
    @Column(name = "total_cost", nullable = false)
    private Long totalCost;

    @Column(length = 500)
    private String notes;

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

    public RentalExternalItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RentalItem getRentalItem() { return rentalItem; }
    public void setRentalItem(RentalItem rentalItem) { this.rentalItem = rentalItem; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Long getUnitCost() { return unitCost; }
    public void setUnitCost(Long unitCost) { this.unitCost = unitCost; }

    public Long getTotalCost() { return totalCost; }
    public void setTotalCost(Long totalCost) { this.totalCost = totalCost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getDesactivationReason() { return desactivationReason; }
    public void setDesactivationReason(String desactivationReason) { this.desactivationReason = desactivationReason; }

    public User getDesactivatedBy() { return desactivatedBy; }
    public void setDesactivatedBy(User desactivatedBy) { this.desactivatedBy = desactivatedBy; }

    public LocalDateTime getDesactivationDate() { return desactivationDate; }
    public void setDesactivationDate(LocalDateTime desactivationDate) { this.desactivationDate = desactivationDate; }
}

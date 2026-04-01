package com.enrique.springboot.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateRentalExternalItemRequest {

    @NotNull(message = "El item de renta es obligatorio")
    private Long rentalItemId;

    @NotNull(message = "El proveedor es obligatorio")
    private Long providerId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

    @NotNull(message = "El costo unitario es obligatorio")
    @Min(value = 1, message = "El costo unitario debe ser mayor a 0")
    private Long unitCost;

    private String notes;

    public Long getRentalItemId() { return rentalItemId; }
    public void setRentalItemId(Long rentalItemId) { this.rentalItemId = rentalItemId; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Long getUnitCost() { return unitCost; }
    public void setUnitCost(Long unitCost) { this.unitCost = unitCost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

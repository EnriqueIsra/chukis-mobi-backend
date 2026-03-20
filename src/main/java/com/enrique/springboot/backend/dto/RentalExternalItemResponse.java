package com.enrique.springboot.backend.dto;

import java.time.LocalDateTime;

public class RentalExternalItemResponse {
    private final Long id;
    private final Long rentalId;
    private final Long providerId;
    private final String providerName;
    private final String description;
    private final Integer quantity;
    private final Long unitCost;
    private final Long totalCost;
    private final String notes;
    private final Boolean active;
    private final String desactivationReason;
    private final String desactivatedByUsername;
    private final LocalDateTime desactivationDate;

    public RentalExternalItemResponse(
            Long id,
            Long rentalId,
            Long providerId,
            String providerName,
            String description,
            Integer quantity,
            Long unitCost,
            Long totalCost,
            String notes,
            Boolean active,
            String desactivationReason,
            String desactivatedByUsername,
            LocalDateTime desactivationDate) {
        this.id = id;
        this.rentalId = rentalId;
        this.providerId = providerId;
        this.providerName = providerName;
        this.description = description;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.notes = notes;
        this.active = active;
        this.desactivationReason = desactivationReason;
        this.desactivatedByUsername = desactivatedByUsername;
        this.desactivationDate = desactivationDate;
    }

    public Long getId() {
        return id;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getUnitCost() {
        return unitCost;
    }

    public Long getTotalCost() {
        return totalCost;
    }

    public String getNotes() {
        return notes;
    }

    public Boolean getActive() {
        return active;
    }

    public String getDesactivationReason() {
        return desactivationReason;
    }

    public String getDesactivatedByUsername() {
        return desactivatedByUsername;
    }

    public LocalDateTime getDesactivationDate() {
        return desactivationDate;
    }
}

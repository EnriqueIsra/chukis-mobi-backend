package com.enrique.springboot.backend.dto;

import java.time.LocalDateTime;

public class ProviderResponse {
    private final Long id;
    private final String name;
    private final String phone;
    private final String notes;
    private final Boolean active;
    private final String desactivationReason;
    private final String desactivatedByUsername;
    private final LocalDateTime desactivationDate;

    public ProviderResponse(
            Long id,
            String name,
            String phone,
            String notes,
            Boolean active,
            String desactivationReason,
            String desactivatedByUsername,
            LocalDateTime desactivationDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.notes = notes;
        this.active = active;
        this.desactivationReason = desactivationReason;
        this.desactivatedByUsername = desactivatedByUsername;
        this.desactivationDate = desactivationDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
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

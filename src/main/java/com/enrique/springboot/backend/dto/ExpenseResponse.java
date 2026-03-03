package com.enrique.springboot.backend.dto;

import java.time.LocalDateTime;

/* Este DTO es lo que el backend le devuelve al frontend. A diferencia del CreateExpenseRequest (que recibe datos), este envía datos. Es inmutable - solo tiene constructor y getters, sin setters.
* Este es un poco más largo porque incluye los campos de borrado lógico y los nombres de usuario (no mandamos el objeto User completo al frontend, solo el username)*/
public class ExpenseResponse {

    private Long id;
    private Long amount;
    private LocalDateTime expenseDate;
    private String category;
    private String description;
    private String imageUrl;
    private Boolean active;
    // En vez de mandar el objeto User completo, mandamos solo el username
    // Así el frontend recibe "Juan" en vez de {id: 1, username: "Juan", password: "...", ...}
    private String username;

    // campos de borrado lógico
    private String desactivationReason;
    private String desactivatedByUsername;
    private LocalDateTime desactivationDate;

    public ExpenseResponse(
            Long id,
            Long amount,
            LocalDateTime expenseDate,
            String category,
            String description,
            String imageUrl,
            Boolean active,
            String username,
            String desactivationReason,
            String desactivatedByUsername,
            LocalDateTime desactivationDate) {
        this.id = id;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.active = active;
        this.username = username;
        this.desactivationReason = desactivationReason;
        this.desactivatedByUsername = desactivatedByUsername;
        this.desactivationDate = desactivationDate;
    }

    // Solo getters - inmutable, no se modifica después de crearse

    public Long getId() {
        return id;
    }

    public Long getAmount() {
        return amount;
    }

    public LocalDateTime getExpenseDate() {
        return expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public String getUsername() {
        return username;
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

/* categoty es String (no el enum) - mandamos "GASOLINA" como texto, el frontend no necesita conocer el enum de Java
* username y desactivatedByUsername son String - extraemos solo el nombre del User, no mandamos contraseñas ni datos sensibles al frontend
* sin setters - una vez creado el objeto no se modifica*/

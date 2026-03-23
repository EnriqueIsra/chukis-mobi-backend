package com.enrique.springboot.backend.dto;

import com.enrique.springboot.backend.enums.ExpenseCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
/* Este es el objeto que recibe los datos del frontend cuando alguien crea o edita un gasto.
* Los DTOs (Data Transfer Object) separan lo que recibe el API de la entidad de base de datos.
* Así controlas exactamente qué campos acepta tu endpoint sin exponer la entidad completa*/
public class CreateExpenseRequest {

    // @NotNull + @Min validan ANTES de que llegue al servicio
    // Si fallan, Spring responde automáticamente con 400 Bad Request
    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private Long amount;

    // Opcional - si llega null, el controller asigna LocalDateTime.now()
    private LocalDateTime expenseDate;

    // @NotNull porque todo gasto debe tener una categoría
    @NotNull(message = "La categoría es obligatoria")
    private ExpenseCategory category;

    // Opcional
    private String description;

    //Opcional - URL de la foto del ticket/comprobante
    private String imageUrl;

    // Quién registra el gasto
    private Long userId;

    // Getters y Setters

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public LocalDateTime getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDateTime expenseDate) {
        this.expenseDate = expenseDate;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}

package com.enrique.springboot.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/* DTO para recibir datos al crear un nuevo pago
* Valida que los campos requeridos estén presentes */
public class CreatePaymentRequest {
    // ID de la renta a la que pertenece el pago
    // @NotNull = Campo obligatorio, no puede ser null
    @NotNull(message = "El ID de la renta es obligatorio")
    private Long rentalId;

    // Monto del pago en pesos
    // @Min(1) = El monto debe ser al menos 1 (no se permiten pagos de $0)
    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private Long amount;

    // Fecha y hora del pago
    // Si es null, el controller puede asignar la fecha actual
    private LocalDateTime paymentDate;

    // Notas opcionales (ej: "Anticipo", "Pago en efectivo", "Transferencia")
    private String notes;

    /* Getters y Setters */

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

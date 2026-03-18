package com.enrique.springboot.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateWorkerPaymentRequest {

    @NotNull(message = "El id del trabajador es obligatorio")
    private Long workerId;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private Long amount;

    private LocalDateTime paymentDate;

    private String notes;

    @NotNull(message = "El id de quien registra es obligatorio")
    private Long registeredById;

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
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

    public Long getRegisteredById() {
        return registeredById;
    }

    public void setRegisteredById(Long registeredById) {
        this.registeredById = registeredById;
    }
}

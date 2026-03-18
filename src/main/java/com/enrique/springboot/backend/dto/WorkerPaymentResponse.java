package com.enrique.springboot.backend.dto;

import java.time.LocalDateTime;

public class WorkerPaymentResponse {

    private final Long id;
    private final Long amount;
    private final LocalDateTime paymentDate;
    private final String notes;
    private final Boolean active;
    private final Long workerId;
    private final String workerUsername;
    private final String workerImageUrl;
    private final String registeredByUsername;
    private final String desactivationReason;
    private final String desactivatedByUsername;
    private final LocalDateTime desactivationDate;

    public WorkerPaymentResponse(
            Long id,
            Long amount,
            LocalDateTime paymentDate,
            String notes,
            Boolean active,
            Long workerId,
            String workerUsername,
            String workerImageUrl,
            String registeredByUsername,
            String desactivationReason,
            String desactivatedByUsername,
            LocalDateTime desactivationDate) {
        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.notes = notes;
        this.active = active;
        this.workerId = workerId;
        this.workerUsername = workerUsername;
        this.workerImageUrl = workerImageUrl;
        this.registeredByUsername = registeredByUsername;
        this.desactivationReason = desactivationReason;
        this.desactivatedByUsername = desactivatedByUsername;
        this.desactivationDate = desactivationDate;
    }

    public Long getId() {
        return id;
    }

    public Long getAmount() {
        return amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public Boolean getActive() {
        return active;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public String getWorkerUsername() {
        return workerUsername;
    }

    public String getWorkerImageUrl() {
        return workerImageUrl;
    }

    public String getRegisteredByUsername() {
        return registeredByUsername;
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

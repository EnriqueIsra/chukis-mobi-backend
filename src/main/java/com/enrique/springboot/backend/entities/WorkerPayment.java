package com.enrique.springboot.backend.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "worker_payments")
public class WorkerPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quién recibe el pago
    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(length = 500)
    private String notes;

    // Quién registró el pago (el admin)
    @ManyToOne
    @JoinColumn(name = "registered_by", nullable = false)
    private User registeredBy;

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

    public WorkerPayment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
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

    public User getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(User registeredBy) {
        this.registeredBy = registeredBy;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDesactivationReason() {
        return desactivationReason;
    }

    public void setDesactivationReason(String desactivationReason) {
        this.desactivationReason = desactivationReason;
    }

    public User getDesactivatedBy() {
        return desactivatedBy;
    }

    public void setDesactivatedBy(User desactivatedBy) {
        this.desactivatedBy = desactivatedBy;
    }

    public LocalDateTime getDesactivationDate() {
        return desactivationDate;
    }

    public void setDesactivationDate(LocalDateTime desactivationDate) {
        this.desactivationDate = desactivationDate;
    }
}
























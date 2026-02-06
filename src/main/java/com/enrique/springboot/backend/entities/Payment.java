package com.enrique.springboot.backend.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
* Entidad para registrar pagos/anticipos de una renta
* Permite multiples pagos parciales con historial de fechas
*/
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Renta asociada al pago
    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    // Monto del pago
    @Column(nullable = false)
    private Long amount;

    // Fecha y hora del pago
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    // Notas opcionales (ej: "Anticipo inicial", "Pago en efectivo", etc)
    @Column(length = 255)
    private String notes;

    // Constructor vacío (requerido por JPA)
    public Payment() {
    }

    // Constructor con parámetros
    public Payment(Rental rental, Long amount, LocalDateTime paymentDate, String notes) {
        this.rental = rental;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.notes = notes;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
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

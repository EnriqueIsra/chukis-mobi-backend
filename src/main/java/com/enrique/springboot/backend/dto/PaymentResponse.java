package com.enrique.springboot.backend.dto;

import java.time.LocalDateTime;

/* DTO para enviar datos de un pago al frontend
* Incluye información de la renta asociada para contexto */
public class PaymentResponse {

    // ID único del pago
    private Long id;

    // Monto del pago
    private Long amount;

    // Fecha y hora en la que se realizó el pago
    private LocalDateTime paymentDate;

    // Notas del pago (ej: "Anticipo", "Pago final")
    private String notes;

    // Información básica de la renta asociada
    private RentalInfo rental;

    /* Constructor completo
    * Recibe todos los datos necesarios para crear la respuesta */

    public PaymentResponse(Long id, Long amount, LocalDateTime paymentDate, String notes, RentalInfo rental) {
        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.notes = notes;
        this.rental = rental;
    }

    /* Getters (no necesitamos setters, es inmutable) */
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

    public RentalInfo getRental() {
        return rental;
    }

    /* DTO interno para información básica de la renta
    * Solo incluimos los campos relevantes, no toda la renta */
    public static class RentalInfo {
        private Long id;        // ID de la renta
        private Long total;     // Total de la renta
        private Long totalPaid; // Total pagado hasta ahora
        private Long pending;   // Saldo pendiente (total - totalPaid)
        private String status;  // Estado de la renta (CREATED, DELIVERED, etc)

        // Constructor que calcula automáticamente el pendiente

        public RentalInfo(Long id, Long total, Long totalPaid, String status) {
            this.id = id;
            this.total = total;
            this.totalPaid = totalPaid;
            this.pending = total - totalPaid;
            this.status = status;
        }

        // Getters

        public Long getId() {
            return id;
        }

        public Long getTotal() {
            return total;
        }

        public Long getTotalPaid() {
            return totalPaid;
        }

        public Long getPending() {
            return pending;
        }

        public String getStatus() {
            return status;
        }
    }
}

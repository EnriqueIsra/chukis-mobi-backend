package com.enrique.springboot.backend.dto;


import java.time.LocalDateTime;

// DTO que representa el detalle de una renta para el dashboard
// Se usa en las secciones "Entregas de hoy" t "Entregas de mañana"
// Contiene información resumida: cliente, productos, pagos, etc.
public class RentalDetailDTO {

    // ID único de la renta (para identificarla al hacer clic)
    private Long id;

    // Nombre del cliente que rentó
    private String clientName;

    // Teléfono del cliente (para contacto rápido)
    private String  clientPhone;

    // Dirección donde se entrega/recoge el mobiliario
    private String address;

    // Fecha y hora de inicio de la renta (cuándo se entrega)
    private LocalDateTime startDate;

    // Fecha y hora de fin de la renta (cunándo se recoge)
    private LocalDateTime endDate;

    // Estado actual de la renta: CREATED, DELIVERED, PICKED_UP, CANCELLED
    private String status;

    // Resumen de productos en texto legible
    // Ejemplo: 3 sillas, 2 mesas, 1 Mantel
    // Esto evita enviar toda la lista de items al frontend
    private String productSummary;

    // Total de la renta en pesos (precio acordado)
    private Long total;

    // Suma de todos los pagos/anticipos registrados
    private Long totalPaid;

    // Lo que falta por cobrar: total - totalPaid
    // Si es 0, la renta está completamente pagada
    private Long pending;

    // Constructor vacío requerido por Spring para serializar/deserializar JSON
    public RentalDetailDTO () {
    }

    // Constructor con todos los campos para crear el DTO fácilmente
    // desde el service cuando armamos la respuesta

    public RentalDetailDTO(Long id, String clientName, String clientPhone,
                           String address, LocalDateTime startDate,
                           LocalDateTime endDate, String status,
                           String productSummary, Long total,
                           Long totalPaid, Long pending) {
        this.id = id;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.productSummary = productSummary;
        this.total = total;
        this.totalPaid = totalPaid;
        this.pending = pending;
    }

    // Getters y Setters
    // Spring necesita los getters para convertir el objeto a JSON
    // y los setters para construir el objeto desde JSON (si fuera necesario)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductSummary() {
        return productSummary;
    }

    public void setProductSummary(String productSummary) {
        this.productSummary = productSummary;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Long totalPaid) {
        this.totalPaid = totalPaid;
    }

    public Long getPending() {
        return pending;
    }

    public void setPending(Long pending) {
        this.pending = pending;
    }
}

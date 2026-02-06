package com.enrique.springboot.backend.entities;

import com.enrique.springboot.backend.enums.RentalStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha y hora inicio de la renta
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    // Fecha y hora fin de la renta
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // Dirección del evento (puede ser distinta a la del cliente)
    @Column(nullable = false)
    private String address;

    // Estado de la renta
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    // Cliente asociado
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Usuario que creó la renta
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Productos rentados
    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentalItem> items;

    // Total calculado
    @Column(nullable = false)
    private Long total;

    /* Pagos/anticipos de la renta
    * - mappedBy = "rental" -> El campo en Payment que tiene el @ManyToOne
    * - cascade = ALL -> Si elimino la renta, se eliminan sus pagos
    * - orphanRemoval = true -> Si quito un pago de la lista, se borra de la BD
    */
    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<RentalItem> getItems() {
        return items;
    }

    public void setItems(List<RentalItem> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    /* Obtiene la lista de pagos/anticipos de esta renta
    * Permite ver el historial completo de pagos*/
    public List<Payment> getPayments() {
        return payments;
    }

    /* Establece la lista de pagos de esta renta
    * Usado internamente por JPA para cargar la relación*/
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}

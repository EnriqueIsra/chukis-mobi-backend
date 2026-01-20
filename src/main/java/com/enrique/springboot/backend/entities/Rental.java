package com.enrique.springboot.backend.entities;

import com.enrique.springboot.backend.enums.RentalStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha inicio de la renta
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // Fecha fin de la renta
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate start_date) {
        this.startDate = start_date;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate end_date) {
        this.endDate = end_date;
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
}

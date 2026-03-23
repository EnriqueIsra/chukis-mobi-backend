package com.enrique.springboot.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "nombre")
    private String nombre;

    @Column(nullable = false, name = "telefono", length = 10)
    private String telefono;

    @Column(nullable = false, name = "direccion")
    private String direccion;

    @Column(name = "email")
    private String email;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getDesactivationReason() { return desactivationReason; }
    public void setDesactivationReason(String desactivationReason) { this.desactivationReason = desactivationReason; }

    public User getDesactivatedBy() { return desactivatedBy; }
    public void setDesactivatedBy(User desactivatedBy) { this.desactivatedBy = desactivatedBy; }

    public LocalDateTime getDesactivationDate() { return desactivationDate; }
    public void setDesactivationDate(LocalDateTime desactivationDate) { this.desactivationDate = desactivationDate; }
}

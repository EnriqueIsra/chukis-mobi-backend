package com.enrique.springboot.backend.entities;

import com.enrique.springboot.backend.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "telefono", length = 10)
    private String telefono;

    @Column(name = "image_url")
    private String imageUrl;

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

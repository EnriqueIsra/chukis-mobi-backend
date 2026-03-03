package com.enrique.springboot.backend.entities;

import com.enrique.springboot.backend.enums.ExpenseCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDateTime expenseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(length = 500)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

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

    // Quién registró el gasto
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Expense() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public LocalDateTime getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDateTime expenseDate) {
        this.expenseDate = expenseDate;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package com.enrique.springboot.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuotationResponse {

    private final Long id;
    private final LocalDateTime createdAt;
    private final String notes;
    private final Long total;
    private final String createdByUsername;
    private final Boolean active;
    private final String desactivationReason;
    private final String desactivatedByUsername;
    private final LocalDateTime desactivationDate;
    private final List<QuotationItemResponse> items;

    public QuotationResponse(Long id, LocalDateTime createdAt, String notes, Long total,
                             String createdByUsername, Boolean active,
                             String desactivationReason, String desactivatedByUsername,
                             LocalDateTime desactivationDate, List<QuotationItemResponse> items) {
        this.id = id;
        this.createdAt = createdAt;
        this.notes = notes;
        this.total = total;
        this.createdByUsername = createdByUsername;
        this.active = active;
        this.desactivationReason = desactivationReason;
        this.desactivatedByUsername = desactivatedByUsername;
        this.desactivationDate = desactivationDate;
        this.items = items;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getNotes() { return notes; }
    public Long getTotal() { return total; }
    public String getCreatedByUsername() { return createdByUsername; }
    public Boolean getActive() { return active; }
    public String getDesactivationReason() { return desactivationReason; }
    public String getDesactivatedByUsername() { return desactivatedByUsername; }
    public LocalDateTime getDesactivationDate() { return desactivationDate; }
    public List<QuotationItemResponse> getItems() { return items; }

    public static class QuotationItemResponse {
        private final Long productId;
        private final String productName;
        private final String productImageUrl;
        private final Integer quantity;
        private final Long price;
        private final Long subtotal;

        public QuotationItemResponse(Long productId, String productName, String productImageUrl,
                                     Integer quantity, Long price) {
            this.productId = productId;
            this.productName = productName;
            this.productImageUrl = productImageUrl;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = (long) quantity * price;
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getProductImageUrl() { return productImageUrl; }
        public Integer getQuantity() { return quantity; }
        public Long getPrice() { return price; }
        public Long getSubtotal() { return subtotal; }
    }
}

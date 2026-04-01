package com.enrique.springboot.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateQuotationRequest {

    @NotNull(message = "Los items son obligatorios")
    private List<QuotationItemRequest> items;

    private String notes;

    @NotNull(message = "El usuario es obligatorio")
    private Long userId;

    // Total personalizado (si el usuario ajusta el precio)
    private Long total;

    public List<QuotationItemRequest> getItems() { return items; }
    public void setItems(List<QuotationItemRequest> items) { this.items = items; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }

    public static class QuotationItemRequest {
        @NotNull
        private Long productId;
        @NotNull
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}

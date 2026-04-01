package com.enrique.springboot.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RentalItemRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;


    private Integer subcontractedQuantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSubcontractedQuantity() {
        return subcontractedQuantity;
    }

    public void setSubcontractedQuantity(Integer subcontractedQuantity) {
        this.subcontractedQuantity = subcontractedQuantity;
    }

}

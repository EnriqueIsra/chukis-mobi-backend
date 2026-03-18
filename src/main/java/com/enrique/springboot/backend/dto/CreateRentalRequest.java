package com.enrique.springboot.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;

public class CreateRentalRequest {

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private Long clientId;

    @NotNull
    private Long userId;

    @NotBlank
    private String address;

    private Long total; // opcional, si viene null el service calcula el total

    @NotEmpty
    private List<RentalItemRequest> items;

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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<RentalItemRequest> getItems() {
        return items;
    }

    public void setItems(List<RentalItemRequest> items) {
        this.items = items;
    }
}

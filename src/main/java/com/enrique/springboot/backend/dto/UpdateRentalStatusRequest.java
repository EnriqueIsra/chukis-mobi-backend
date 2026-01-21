package com.enrique.springboot.backend.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateRentalStatusRequest {

    @NotNull
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.enrique.springboot.backend.dto;

import java.time.LocalDate;

public class RentalResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long total;

    public RentalResponse(Long id, LocalDate startDate, LocalDate endDate, String status, Long total) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public Long getTotal() {
        return total;
    }
}

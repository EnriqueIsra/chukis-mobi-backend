package com.enrique.springboot.backend.dto;

import java.util.List;

public class WorkerMonthlySummaryResponse {
    private final Long workerId;
    private final String workerUsername;
    private final String workerImageUrl;
    private final Long totalAmount;
    private final List<WorkerPaymentResponse> payments;

    public WorkerMonthlySummaryResponse(
            Long workerId,
            String workerUsername,
            String workerImageUrl,
            Long totalAmount,
            List<WorkerPaymentResponse> payments) {
        this.workerId = workerId;
        this.workerUsername = workerUsername;
        this.workerImageUrl = workerImageUrl;
        this.totalAmount = totalAmount;
        this.payments = payments;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public String getWorkerUsername() {
        return workerUsername;
    }

    public String getWorkerImageUrl() {
        return workerImageUrl;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public List<WorkerPaymentResponse> getPayments() {
        return payments;
    }
}

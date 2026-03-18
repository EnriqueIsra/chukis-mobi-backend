package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.WorkerMonthlySummaryResponse;
import com.enrique.springboot.backend.dto.WorkerPaymentResponse;
import com.enrique.springboot.backend.entities.WorkerPayment;

import java.util.List;
import java.util.Optional;

public interface WorkerPaymentService {
    List<WorkerPaymentResponse> findAllActive();
    List<WorkerPaymentResponse> findAllInactive();
    List<WorkerPaymentResponse> findByWorkerId(Long workerId);
    List<WorkerMonthlySummaryResponse> getMonthlySummaryByWorker(int year, int month);
    Optional<WorkerPayment> findById(Long id);
    WorkerPayment save(WorkerPayment payment);
    WorkerPaymentResponse deactivate(Long id, String reason, Long desactivatedByUserId);
    WorkerPaymentResponse activate(Long id);
}

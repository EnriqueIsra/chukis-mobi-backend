package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.MonthlySummaryResponse;
import com.enrique.springboot.backend.repositories.ExpenseRepository;
import com.enrique.springboot.backend.repositories.PaymentRepository;
import com.enrique.springboot.backend.repositories.RentalExternalItemRepository;
import com.enrique.springboot.backend.repositories.WorkerPaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200/"})
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final WorkerPaymentRepository workerPaymentRepository;
    private final RentalExternalItemRepository externalItemRepository;

    public ReportController(PaymentRepository paymentRepository,
                            ExpenseRepository expenseRepository,
                            WorkerPaymentRepository workerPaymentRepository,
                            RentalExternalItemRepository externalItemRepository) {
        this.paymentRepository = paymentRepository;
        this.expenseRepository = expenseRepository;
        this.workerPaymentRepository = workerPaymentRepository;
        this.externalItemRepository = externalItemRepository;
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month) {

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        Long totalIncome = paymentRepository.sumAmountByDateRange(start, end);
        Long totalExpenses = expenseRepository.sumActiveExpensesByDateRange(start, end);
        Long totalPayroll = workerPaymentRepository.sumAllActivePaymentsByDateRange(start, end);
        Long totalSubcontracted = externalItemRepository.sumActiveTotalCostByDateRange(start, end);

        MonthlySummaryResponse response = new MonthlySummaryResponse(
                year, month, totalIncome, totalExpenses, totalPayroll, totalSubcontracted
        );

        return ResponseEntity.ok(response);
    }
}

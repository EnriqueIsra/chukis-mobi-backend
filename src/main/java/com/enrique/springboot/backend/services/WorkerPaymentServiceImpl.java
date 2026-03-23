package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.WorkerMonthlySummaryResponse;
import com.enrique.springboot.backend.dto.WorkerPaymentResponse;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.entities.WorkerPayment;
import com.enrique.springboot.backend.repositories.UserRepository;
import com.enrique.springboot.backend.repositories.WorkerPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkerPaymentServiceImpl implements WorkerPaymentService {

    private final WorkerPaymentRepository workerPaymentRepository;
    private final UserRepository userRepository;

    public WorkerPaymentServiceImpl(
            WorkerPaymentRepository workerPaymentRepository,
            UserRepository userRepository) {
        this.workerPaymentRepository = workerPaymentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerPaymentResponse> findAllActive() {
        return workerPaymentRepository.findByActiveTrueOrderByPaymentDateDesc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerPaymentResponse> findAllInactive() {
        return workerPaymentRepository.findByActiveFalseOrderByPaymentDateDesc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerPaymentResponse> findByWorkerId(Long workerId) {
        return workerPaymentRepository.findByActiveTrueAndWorkerIdOrderByPaymentDateDesc(workerId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerMonthlySummaryResponse> getMonthlySummaryByWorker(int year, int month) {
        // Calcular inicio y fin del mes
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        List<WorkerPayment> payments = workerPaymentRepository
                .findByActiveTrueAndPaymentDateBetweenOrderByPaymentDateDesc(start, end);

        // Agrupar por trabajadores
        return payments.stream()
                .collect(Collectors.groupingBy(p -> p.getWorker().getId()))
                .entrySet().stream()
                .map(entry -> {
                    Long workerId = entry.getKey();
                    List<WorkerPayment> workerPayments = entry.getValue();
                    User worker = workerPayments.get(0).getWorker();
                    Long total = workerPaymentRepository.sumActivePaymentsByWorkerAndDateRange(workerId, start, end);
                    List<WorkerPaymentResponse> paymentResponses = workerPayments.stream()
                            .map(this::toResponse).toList();
                    return new WorkerMonthlySummaryResponse(
                            worker.getId(),
                            worker.getUsername(),
                            worker.getImageUrl(),
                            total,
                            paymentResponses
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerMonthlySummaryResponse> getInactiveMonthlySummaryByWorker(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        List<WorkerPayment> payments = workerPaymentRepository
                .findByActiveFalseAndPaymentDateBetweenOrderByPaymentDateDesc(start, end);

        return payments.stream()
                .collect(Collectors.groupingBy(p -> p.getWorker().getId()))
                .entrySet().stream()
                .map(entry -> {
                    Long workerId = entry.getKey();
                    List<WorkerPayment> workerPayments = entry.getValue();
                    User worker = workerPayments.get(0).getWorker();
                    Long total = workerPaymentRepository.sumInactivePaymentsByWorkerAndDateRange(workerId, start, end);
                    List<WorkerPaymentResponse> paymentResponses = workerPayments.stream()
                            .map(this::toResponse).toList();
                    return new WorkerMonthlySummaryResponse(
                            worker.getId(),
                            worker.getUsername(),
                            worker.getImageUrl(),
                            total,
                            paymentResponses
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkerPayment> findById(Long id) {
        return workerPaymentRepository.findById(id);
    }

    @Override
    @Transactional
    public WorkerPayment save(WorkerPayment payment) {
        return workerPaymentRepository.save(payment);
    }

    @Override
    @Transactional
    public WorkerPaymentResponse deactivate(Long id, String reason, Long desactivatedByUserId) {
        WorkerPayment payment = workerPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + id));

        payment.setActive(false);
        payment.setDesactivationReason(reason);
        payment.setDesactivationDate(LocalDateTime.now());

        Optional<User> optionalUser = userRepository.findById(desactivatedByUserId);
        optionalUser.ifPresent(payment::setDesactivatedBy);
        return toResponse(workerPaymentRepository.save(payment));
    }

    @Override
    @Transactional
    public WorkerPaymentResponse activate(Long id) {
        WorkerPayment payment = workerPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + id));

        payment.setActive(true);
        payment.setDesactivationReason(null);
        payment.setDesactivationDate(null);
        payment.setDesactivatedBy(null);

        return toResponse(workerPaymentRepository.save(payment));
    }

    // Convierte entidad a DTO de respuesta
    private WorkerPaymentResponse toResponse(WorkerPayment payment) {
        return new WorkerPaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getNotes(),
                payment.isActive(),
                payment.getWorker().getId(),
                payment.getWorker().getUsername(),
                payment.getWorker().getImageUrl(),
                payment.getRegisteredBy().getUsername(),
                payment.getDesactivationReason(),
                payment.getDesactivatedBy() != null ? payment.getDesactivatedBy().getUsername() : null,
                payment.getDesactivationDate()
        );
    }
}


















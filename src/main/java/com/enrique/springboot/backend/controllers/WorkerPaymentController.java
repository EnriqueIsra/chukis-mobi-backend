package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreateWorkerPaymentRequest;
import com.enrique.springboot.backend.dto.WorkerMonthlySummaryResponse;
import com.enrique.springboot.backend.dto.WorkerPaymentResponse;
import com.enrique.springboot.backend.entities.WorkerPayment;
import com.enrique.springboot.backend.repositories.UserRepository;
import com.enrique.springboot.backend.services.WorkerPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/worker-payments")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200"})
public class WorkerPaymentController {

    private final WorkerPaymentService workerPaymentService;
    private final UserRepository userRepository;

    public WorkerPaymentController(
            WorkerPaymentService workerPaymentService,
            UserRepository userRepository) {
        this.workerPaymentService = workerPaymentService;
        this.userRepository = userRepository;
    }

    // Todos los activos
    @GetMapping
    public ResponseEntity<List<WorkerPaymentResponse>> findAll() {
        return ResponseEntity.ok(workerPaymentService.findAllActive());
    }

    // Todos los inactivos
    @GetMapping("/inactive")
    public ResponseEntity<List<WorkerPaymentResponse>> findInactive() {
        return ResponseEntity.ok(workerPaymentService.findAllInactive());
    }

    // Por trabajador
    @GetMapping("/by-worker/{workerId}")
    public ResponseEntity<List<WorkerPaymentResponse>> findByWorker(
            @PathVariable Long workerId
    ) {
        return ResponseEntity.ok(workerPaymentService.findByWorkerId(workerId));
    }

    // Para el resumen mensual
    @GetMapping("/monthly-summary")
    public ResponseEntity<List<WorkerMonthlySummaryResponse>> monthlySummary(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(workerPaymentService.getMonthlySummaryByWorker(year, month));
    }

    // Registrar nuevo pago
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateWorkerPaymentRequest request) {
        WorkerPayment payment = new WorkerPayment();

        // Asignar trabajador
        Optional<com.enrique.springboot.backend.entities.User>
                worker = userRepository.findById(request.getWorkerId());
        if (worker.isEmpty()) {
            return ResponseEntity.badRequest().body("Trabajador no encontrado");
        }
        payment.setWorker(worker.get());

        // Asignar quien registra
        Optional<com.enrique.springboot.backend.entities.User> registeredBy =
                userRepository.findById(request.getRegisteredById());
        if (registeredBy.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario registrador no encontrado");
        }
        payment.setRegisteredBy(registeredBy.get());

        payment.setAmount(request.getAmount());
        // Si no viene fecha, usar la fecha actual
        payment.setPaymentDate(request.getPaymentDate() != null
                ? request.getPaymentDate()
                : LocalDateTime.now());
        payment.setNotes(request.getNotes());

        WorkerPayment saved = workerPaymentService.save(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody CreateWorkerPaymentRequest request) {
        Optional<WorkerPayment> optionalWorkerPayment = workerPaymentService.findById(id);
        if (optionalWorkerPayment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        WorkerPayment payment = optionalWorkerPayment.get();
        Optional<com.enrique.springboot.backend.entities.User> worker =
                userRepository.findById(request.getWorkerId());
        if (worker.isEmpty()) {
            return ResponseEntity.badRequest().body("Trabajador no encontrado");
        }
        payment.setWorker(worker.get());

        payment.setAmount(request.getAmount());
        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        }
        payment.setNotes(request.getNotes());

        workerPaymentService.save(payment);
        return ResponseEntity.ok().build();
    }

    // Desactivar
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body) {
        String reason = (String) body.get("reason");
        Long userId = Long.valueOf(body.get("userId").toString());
        return ResponseEntity.ok(workerPaymentService.deactivate(id, reason, userId));
    }

    // Activar
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        return ResponseEntity.ok(workerPaymentService.activate(id));
    }
}




















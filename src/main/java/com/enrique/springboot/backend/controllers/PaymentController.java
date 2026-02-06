package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreatePaymentRequest;
import com.enrique.springboot.backend.dto.PaymentResponse;
import com.enrique.springboot.backend.dto.PaymentResponse.RentalInfo;
import com.enrique.springboot.backend.entities.Payment;
import com.enrique.springboot.backend.entities.Rental;
import com.enrique.springboot.backend.repositories.RentalRepository;
import com.enrique.springboot.backend.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/* Controller REST para operaciones de pagos/anticipos
* Base URL: /api/payments */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200"})
public class PaymentController {

    // Servicio de pagos - contiene la lógica de negocio
    private final PaymentService paymentService;

    // Repositorio de rentas - para buscar la renta al crear un pago
    private final RentalRepository rentalRepository;

    /* Constructor con inyección de dependencias */
    public PaymentController(PaymentService paymentService, RentalRepository rentalRepository) {
        this.paymentService = paymentService;
        this.rentalRepository = rentalRepository;
    }

    // GET /api/payments/rental/{rentalId}
    // Obtiene todos los pagos de una renta específica
    // Uso: Mostrar el historial de pagos en el detalle de una renta
    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByRentalId(@PathVariable Long rentalId) {
        // Verificar que la renta existe
        Optional<Rental> optionalRental = rentalRepository.findById(rentalId);
        if (optionalRental.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener pagos de la renta
        List<Payment> payments = paymentService.findByRentalId(rentalId);

        // Obtener datos de la renta para incluir en cada respuesta
        Rental rental = optionalRental.get();
        Long totalPaid = paymentService.getTotalPaidByRentalId(rentalId);

        // Convertir entidades a DTOs
        List<PaymentResponse> response = payments.stream()
                .map(payment -> toPaymentResponse (payment, rental, totalPaid))
                .toList();

        return ResponseEntity.ok(response);
    }

    // GET /api/payments/{id}
    // Obtiene un pago especifico por su id
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        Optional<Payment> optionalPayment = paymentService.findById(id);

        if (optionalPayment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Payment payment = optionalPayment.get();
        Rental rental = payment.getRental();
        Long totalPaid = paymentService.getTotalPaidByRentalId(rental.getId());

        return ResponseEntity.ok(toPaymentResponse(payment, rental, totalPaid));
    }

    // POST /api/payments
    // Crea un nuevo pago/anticipo
    // @Valid activa las validaciones del DTO (@NotNull, @Min, etc.)
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        // Buscar la renta asociada
        Optional<Rental> optionalRental = rentalRepository.findById(request.getRentalId());
        if (optionalRental.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Rental rental = optionalRental.get();

        // Crear la entidad Payment
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setAmount(request.getAmount());
        payment.setNotes(request.getNotes());

        // Si no se proporciona fecha, usar la fecha/hora actual
        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        } else {
            payment.setPaymentDate(LocalDateTime.now());
        }

        // Guardar el pago
        Payment savedPayment = paymentService.save(payment);

        // Calcular el nuevo total pagado (incluyendo este pago)
        Long totalPaid = paymentService.getTotalPaidByRentalId(rental.getId());

        // Retornal la respuesta con status 201 (Created)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toPaymentResponse(savedPayment, rental, totalPaid));
    }

    // DELETE /api/payments/{id}
    // Elimina un pago por su ID
    // Uso: Corregir un pago registrado por error
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentById(@PathVariable Long id) {
        Optional<Payment> paymentDeleted = paymentService.deleteById(id);

        if (paymentDeleted.isEmpty()){
            // Si no existía, retornar 404
            return ResponseEntity.notFound().build();
        }

        // Retornar 204 (No Content) - eliminación exitosa
        return ResponseEntity.noContent().build();
    }

    // GET /api/paymets/rental/{rentalId}/summary
    // Obtiene resumen de pagos de una renta (total, pagado, pendiente)
    // Uso: Mostrar resumen rápido sin el historial completo
    @GetMapping("/rental/{rentalId}/summary")
    public ResponseEntity<RentalInfo> getPaymentSummary(@PathVariable Long rentalId) {
        Optional<Rental> optionalRental = rentalRepository.findById(rentalId);

        if (optionalRental.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Rental rental = optionalRental.get();
        Long totalPaid = paymentService.getTotalPaidByRentalId(rentalId);

        RentalInfo summary = new RentalInfo(
                rental.getId(),
                rental.getTotal(),
                totalPaid,
                rental.getStatus().name()
        );
        return ResponseEntity.ok(summary);
    }
    // Método auxiliar: Convierte Payment entity a PaymentResponse DTO
    // Centraliza la lógica de mapeo para evitar duplicación
    private PaymentResponse toPaymentResponse(Payment payment, Rental rental, Long totalPaid) {
        RentalInfo rentalInfo = new RentalInfo(
                rental.getId(),
                rental.getTotal(),
                totalPaid,
                rental.getStatus().name()
        );

        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getNotes(),
                rentalInfo
        );
    }
}

/*
* GET -> /api/paymets/rental/{rentalId} -> Lista de pagos de una renta
* GET -> /api/paymets/{id} -> Obtiene un pago por id
* POST -> /api/paymets -> Crea nuevo pago
* DELETE -> /api/payments/{id} -> Elimina un pago
* GET -> /api/paymets/rental/{rentalId}/summary -> Resumen de pagos */























package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Payment;
import com.enrique.springboot.backend.entities.Rental;
import com.enrique.springboot.backend.repositories.PaymentRepository;
import com.enrique.springboot.backend.repositories.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/* Implementaciòn del servicio de pagos
* Contiene la lògica de negocio para operaciones con pagos/anticipos */
@Service
public class PaymentServiceImpl implements PaymentService {

    // Repositorio de pagos - inyectado por constructor
    private final PaymentRepository paymentRepository;

    // Repositorio de rentas- necesario para calcular saldo pendiente
    private final RentalRepository rentalRepository;

    // Constructor con inyección de dependencias
    // Spring automáticamente inyecta los repositorios
    public PaymentServiceImpl(PaymentRepository paymentRepository, RentalRepository rentalRepository) {
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
    }

    /* CRUD básico */
    @Override
    @Transactional(readOnly = true) // Solo lectura, optimiza rendimiento
    public List<Payment> findAll() {
        // Convierte Iterable a List para facilitar su uso
        return (List<Payment>) paymentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findById(Long id) {
        // Optional maneja el caso de "no encontrado" sin lanzar excepción
        return paymentRepository.findById(id);
    }

    @Override
    @Transactional // Transacción de estritura
    public Payment save(Payment payment) {
        // save() funciona para crear (id = null) y actualizar (id existente)
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Optional<Payment> deleteById(Long id) {
        // Primero buscamos el pago para retornarlo después de eliminarlo
        Optional<Payment> optionalPayment = paymentRepository.findById(id);

        // Si existe, lo eliminamos
        optionalPayment.ifPresent(paymentRepository::delete);

        // Retornamos el pago eliminado (o vacío si no existía)
        return optionalPayment;
    }

    /* OPERACIONES POR RENTA */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByRentalId(Long rentalId) {
        // Retorna pagos ordenados cronológicamente
        return paymentRepository.findByRentalIdOrderByPaymentDateAsc(rentalId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalPaidByRentalId(Long rentalId) {
        // Usa el query del repositorio que suma todos los montos
        return paymentRepository.sumAmountByRentalId(rentalId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPendingBalanceByRentalId(Long rentalId) {
        // Buscamos la renta para obtener su total
        Optional<Rental> optionalRental = rentalRepository.findById(rentalId);

        if (optionalRental.isEmpty()) {
            // Si la renta no existe, retornamos 0
            return 0L;
        }

        // Total de la renta (lo que debe pagar el cliente)
        Long rentalTotal = optionalRental.get().getTotal();

        // Total ya pagado (suma de todos los pagos/anticipos)
        Long totalPaid = paymentRepository.sumAmountByRentalId(rentalId);

        // Saldo pendiente = Total - Pagado
        // Ejemplo: Renta $5000 - Pagado $3000 = Pendiente $2000
        return rentalTotal - totalPaid;
    }

    /* OPERACIONES POR FECHA */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // Retorna todos los pagos en el rango de fechas
        return paymentRepository.findPaymentDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Long sumByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // Suma total de pagos en el periodo (para ingresos del mes)
        return paymentRepository.sumAmountByDateRange(startDate, endDate);
    }
}

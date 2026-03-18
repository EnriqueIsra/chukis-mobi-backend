package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.WorkerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkerPaymentRepository extends JpaRepository<WorkerPayment, Long> {

    // Todos los pagos activos, más recientes primero
    List<WorkerPayment> findByActiveTrueOrderByPaymentDateDesc();

    // Todos los pagos desactivados
    List<WorkerPayment> findByActiveFalseOrderByPaymentDateDesc();

    // Pagos activos de un trabajador específico
    List<WorkerPayment> findByActiveTrueAndWorkerIdOrderByPaymentDateDesc(Long workerId);

    // Pagos activos en un rango de fechas (para la vista mensual)
    List<WorkerPayment> findByActiveTrueAndPaymentDateBetweenOrderByPaymentDateDesc(
            LocalDateTime start, LocalDateTime end
    );

    // Pagos activos de un trabajador en un rango de fechas
    List<WorkerPayment> findByActiveTrueAndWorkerIdAndPaymentDateBetweenOrderByPaymentDateDesc(
            Long workerId, LocalDateTime start, LocalDateTime end
    );

    // Suma total de pagos activos de un trabajador en un rango de fechas
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM WorkerPayment p " +
        "WHERE p.active = true AND p.worker.id = :workerId " +
        "AND p.paymentDate >= :startDate AND p.paymentDate < :endDate")
    Long sumActivePaymentsByWorkerAndDateRange(
            @Param("workerId") Long workerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}




















package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/* Interface que define las operaciones disponibles para Pagos */
public interface PaymentService {
    /* CRUD básico */

    // Obtiene todos los pagos del sistema
    // Uso: Administración general (poco común usarlo)
    List<Payment> findAll();

    // Busca un pago por su ID
    // Retorna optional para manejar el caso "no encontrado"
    Optional<Payment> findById(Long id);

    // Guarda un nuevo pago o actualiza uno existente
    // Si el payment tiene id = null, crea uno nuevo
    // Si tiene id, actualiza el existente
    Payment save(Payment payment);

    // Elimina un pago por su ID
    // Retorna el pago eliminado o vacío si no existía
    Optional<Payment> deleteById(Long id);

    /* OPERACIONES POR RENTA */

    // Obtiene todos los pagos de una renta específica
    // Ordenados por fecha (más antiguo primero)
    List<Payment> findByRentalId(Long rentalId);

    // Calcula el total de pagos de una renta
    // Suma todos los montos de los pagos de esa renta
    Long getTotalPaidByRentalId (Long rentalId);

    // Calcula el saldo pendiente de una renta
    // Fórmula: total de la renta - suma de pagos
    Long getPendingBalanceByRentalId (Long rentalId);

    /* OPERACIONES POR FECHA (para dashboard) */

    // Obtiene pagos realizados en un rango de fechas
    List<Payment> findByDateRange (LocalDateTime startDate, LocalDateTime endDate);

    // Suma total de pagos en un rango de fechas
    Long sumByDateRange (LocalDateTime startDate, LocalDateTime endDate);
}

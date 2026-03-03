package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Expense;
import com.enrique.springboot.backend.enums.ExpenseCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/* La interfaz define el contrato - qué operaciones están disponibles. Cualquier clase que la implemente debe tener estos métodos.
Esto permite que si mañana quieres cambiar la implementación, el resto de código no se afecta*/
public interface ExpenseService {

    // Gastos activos (los que se muestran por defecto)
    List<Expense> findAllActive();

    // Gastos desactivados (para el botón "ver desactivados")
    List<Expense> findAllInactive();

    Optional<Expense> findById(Long id);

    Expense save(Expense expense);

    // Borrado lógico: pone active = falso y llena el motivo, quién y cuándo
    Optional<Expense> deactivate(Long id, String reason, Long desactivatedByUserId);

    // Reactivar: pone active = true y limpia los campos de baja
    Optional<Expense> activate(Long id);

    // Filtrar por categoría (solo activos)
    List<Expense> findByCategory(ExpenseCategory category);

    // Para reportes futuros
    Long sumActiveByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}

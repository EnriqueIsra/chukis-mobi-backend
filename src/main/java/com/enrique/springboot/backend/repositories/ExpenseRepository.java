package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.Expense;
import com.enrique.springboot.backend.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    // Gastos ACTIVOS ordenados del más reciente al más antiguo
    // Spring genera el SQL automáticamente a partir del nombre del método:
    // "findBy" + "Active" (campo) + "True" (valor) + "orderBy" + "ExpenseDate" + "Desc"
    // Equivale a: SELECT * FROM expenses WHERE active = true ORDER BY expense_date DESC
    List<Expense> findByActiveTrueOrderByExpenseDateDesc();

    // Gastos DESACTIVADOS (borrado lógico) - para el botón "ver desactivados"
    List<Expense> findByActiveFalseOrderByExpenseDateDesc();

    // Filtra gastos activos por categoría
    // Ejemplo: findByActiveTrueAndCategory(ExpenseCategory.GASOLINA)
    // -> solo gastos de gasolina que estén activos
    List<Expense> findByActiveTrueAndCategory(ExpenseCategory category);

    // Suma total de gastos ACTIVOS en un rango de fechas
    // COALESCE: si no hay gastos en ese periodo, retorna 0 en vez de null
    // Uso futuro: reportes - "¿Cuánto se gastó este mes?"
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.active = true AND e.expenseDate >= :startDate AND e.expenseDate < :endDate")
    Long sumActiveExpensesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

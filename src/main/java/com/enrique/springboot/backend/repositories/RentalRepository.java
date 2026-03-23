package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.Rental;
import com.enrique.springboot.backend.enums.RentalStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RentalRepository extends CrudRepository<Rental, Long> {

    // Rentas que se cruzan con un rango de fechas y están activas
    @Query("""
                SELECT r FROM Rental r
                WHERE r.status IN :statuses
                AND r.startDate <= :endDate
                AND r.endDate >= :startDate
            """)
    List<Rental> findActiveRentalsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("statuses") List<RentalStatus> statuses);

    // Contar rentas por status
    Long countByStatus(RentalStatus status);

    // Sumar totales de rentas en un rango de fechas (para ingresos del mes)
    @Query("""
            SELECT COALESCE(SUM(r.total), 0) FROM Rental r
            where r.startDate >= :startDate
            and r.startDate < :endDate
            and r.status IN :statuses
            """)
    Long sumTotalByDateRangeAndStatuses(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<RentalStatus> statuses
    );

    // Query para "Entregas de hoy" y "Entregas de mañana"
    // Busca rentas cuya fecha de INICIO (startDate) caiga dentro de un rango de fechas y tenga un estado específico.
    // Ejemplo de uso:
    //  startDate = 2026-02-12 00:00:00 (inicio del día)
    //  endDate = 2026-02-13 00:00:00 (inicio del día siguiente)
    //  statuses = [CREATED] (pendientes de entregar)
    // Esto nos da todas las rentas que se deben ENTREGAR hoy
    // y que aún no han sido entregadas (status CREATED)
    // ORDER BY r.startDate ASC ordena por hora de entrega,
    // para que las más próximas aparezcan primero.
    // Languaje=JPAQL
    @Query("""
        SELECT r FROM Rental r
        WHERE r.startDate >= :startDate
        AND r.startDate < :endDate
        AND r.status IN :statuses
        ORDER BY r.startDate ASC
        """)
    List<Rental> findByStartDateRangeAndStatuses(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<RentalStatus> statuses
    );

    // Query para la gráfica de ingresos del dashboard
    // Busca rentas en un rango de fechas CON sus pagos incluidos.
    // LEFT JOIN FETCH r.payments:
    // - LEFT JOIN: incluye rentas aunque no tengan pagos
    // - FETCH: carga los pagos junto con la renta en UNA sola consulta SQL (Sin esto, hibernate haría una consulta
    // extra por cada renta para obtener sus pagos = lento)
    // DISTINCT: evita rentas duplicadas.
    // Cuando una renta tiene 3 pagos, el JOIN genera 3 filas para la misma renta. DISTINCT elimina las repetidas
    // Se una para calcular por cada día:
    // - cobrado: total de rentas 100% pagadas
    // - anticipos: pagos parciales de rentas no completadas
    // - porCobrar: lo que falta por cobrar.
    @Query("""
        SELECT DISTINCT r FROM Rental r
        LEFT JOIN FETCH r.payments
        WHERE r.startDate >= :startDate
        AND r.startDate < :endDate
        AND r.status IN :statuses
        ORDER BY r.startDate ASC
        """)
    List<Rental> findRentalsWithPaymentsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<RentalStatus> statuses
    );

    // Buscar rentas por status
    // Spring Data genera el query automáticamente a partir del nombre:
    // find + By + Status -> SELECT * FROM rentals WHERE status = ?
    // Se usa en el dashboard para obtener la lista de rentas CREATED
    List<Rental> findByStatus(RentalStatus status);

    List<Rental> findByActiveTrueOrderByStartDateDesc();

    List<Rental> findByActiveFalseOrderByStartDateDesc();
}

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

}

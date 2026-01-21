package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.RentalItem;
import com.enrique.springboot.backend.enums.RentalStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RentalItemRepository extends CrudRepository<RentalItem, Long> {

    // Total de unidades rentadas de un producto en un rango de fechas y horas
    @Query("""
            SELECT COALESCE(SUM(ri.quantity), 0)
            FROM RentalItem ri
            JOIN ri.rental r
            WHERE ri.product.id = :productId
            AND r.status IN :statuses
            AND r.startDate <= :endDate
            AND r.endDate >= :startDate
            """)
    Long getRentedQuantityByProductAndDates(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<RentalStatus> statuses
    );

    // Total de unidades rentadas excluyendo una renta específica (para edición)
    @Query("""
            SELECT COALESCE(SUM(ri.quantity), 0)
            FROM RentalItem ri
            JOIN ri.rental r
            WHERE ri.product.id = :productId
            AND r.status IN :statuses
            AND r.startDate <= :endDate
            AND r.endDate >= :startDate
            AND r.id <> :excludeRentalId
            """)
    Long getRentedQuantityByProductAndDatesExcludingRental(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<RentalStatus> statuses,
            @Param("excludeRentalId") Long excludeRentalId
    );
}

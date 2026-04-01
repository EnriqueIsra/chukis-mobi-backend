package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.RentalExternalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RentalExternalItemRepository extends JpaRepository<RentalExternalItem, Long> {

    // Todos los activos
    List<RentalExternalItem> findByActiveTrueOrderByIdDesc();

    // Todos los inactivos
    List<RentalExternalItem> findByActiveFalseOrderByIdDesc();

    // Items activos de un RentalItem específico
    List<RentalExternalItem> findByActiveTrueAndRentalItemId(Long rentalItemId);

    // Items activos de una renta (vía rentalItem.rental.id)
    List<RentalExternalItem> findByActiveTrueAndRentalItemRentalId(Long rentalId);

    // Suma de cantidad asignada a proveedores para un RentalItem (para validar límite)
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM RentalExternalItem i " +
           "WHERE i.active = true AND i.rentalItem.id = :rentalItemId")
    Long sumActiveQuantityByRentalItemId(@Param("rentalItemId") Long rentalItemId);

    // Suma del costo total de una renta completa (para rentabilidad)
    @Query("SELECT COALESCE(SUM(i.totalCost), 0) FROM RentalExternalItem i " +
           "WHERE i.active = true AND i.rentalItem.rental.id = :rentalId")
    Long sumActiveTotalCostByRentalId(@Param("rentalId") Long rentalId);

    // Suma total de costos externos activos en un rango de fechas (para reportes)
    @Query("SELECT COALESCE(SUM(i.totalCost), 0) FROM RentalExternalItem i " +
           "WHERE i.active = true AND i.rentalItem.rental.startDate >= :startDate " +
           "AND i.rentalItem.rental.startDate < :endDate")
    Long sumActiveTotalCostByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

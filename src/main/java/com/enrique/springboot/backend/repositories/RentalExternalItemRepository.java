package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.RentalExternalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentalExternalItemRepository extends JpaRepository<RentalExternalItem, Long> {
    List<RentalExternalItem> findByActiveTrueOrderByIdDesc();
    List<RentalExternalItem> findByActiveFalseOrderByIdDesc();

    // Todos los items activos de una renta específica
    List<RentalExternalItem> findByActiveTrueAndRentalId(Long rentalId);

    // Suma del costo total de los items activos de una renta (para rentabilidad)
    @Query("SELECT COALESCE(SUM(i.totalCost), 0) FROM RentalExternalItem i " +
            "WHERE i.active = true AND i.rental.id = :rentalId")
    Long sumActiveTotalCostByRentalId(@Param("rentalId") Long rentalId);

}

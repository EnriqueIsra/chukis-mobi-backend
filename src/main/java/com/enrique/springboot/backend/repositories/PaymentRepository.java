package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/* Repositorio para operaciones CRUD de pagos/anticipos
* Extiende CrudRepository que provee: save, findById, findAllm, delete, etc. */
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    // Busca todos los pagos de una renta específica
    // Uso: Ver historial de pagos de una renta
    // Ejemplo: findByRentalId(5L) -> Lista de pagos de la renta #5
    List<Payment> findByRentalId(Long rentalId);

    // Buscar pagos por renta ordenados por fecha ascendente
    // Uso: Mostrar historial cronológico de pagos
    // El más antiguo primero, el más reciente al final
    List<Payment> findByRentalIdOrderByPaymentDateAsc(Long rentalId);


    // Calcula la suma total de todos los pagos de una renta
    // COALESCE: Si no hay pagos, retorna 0 en lugar de null
    // Uso: Saber cuánto ha pagado el cliente en total
    // Ejemplo: Renta de $5000, sumAmountByRentalId = $3000 -> debe $2000
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p where p.rental.id = :rentalId")
    Long sumAmountByRentalId(@Param("rentalId") Long rentalId);


    // Busca pagos realizados en un rango de fechas
    // Uso: Obtener pagos del mes para la gráfica del dashboard
    // startDate incluido, endDate excluido (>= y <)
    // Ejemplo: Pagos del 1 al 28 de febrero
    @Query("SELECT p FROM Payment p where p.paymentDate >= :startDate AND p.paymentDate < :endDate")
    List<Payment> findPaymentDateBetween(
            @Param("startDate")LocalDateTime startDate,
            @Param("endDate")LocalDateTime endDate
            );

    // Suma de pagos en un rango de fechas
    // Uso: Calcular ingresos totales del mes en el dashboard
    // COALESCE: Retorna 0 si no hay pagos en ese periodo
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p where p.paymentDate >= :startDate AND p.paymentDate <  :endDate")
    Long sumAmountByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

/*
* Métodos incluidos
* findByRentalId -> Ver todos los pagos de una renta
* fingByRentalIdOrderByPaymentDateAsc -> Historial de pagos ordenado
* sumAmountByRentalId -> Calcular total pagado de una renta
* findByPaymentDateBeetween -> Para la gráfica de ingresos del mes
* sumAmountByDateRange -> Total de ingresos en un periodo
*/

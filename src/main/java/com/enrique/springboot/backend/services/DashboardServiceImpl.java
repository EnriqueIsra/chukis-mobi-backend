package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.DashboardStatsResponse;
import com.enrique.springboot.backend.enums.RentalStatus;
import com.enrique.springboot.backend.repositories.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * Implementación del servicio de dashboard
 * Calcula las estadísticas usando el RentalRepository
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    // Inyectamos el repositorio de rentas
    private final RentalRepository rentalRepository;

    // Constructor con inyección de dependencias

    public DashboardServiceImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)     // Solo lectura, no modifica datos
    public DashboardStatsResponse getStats() {

        // 1.- Contar rentas por entregar (status = CREATED)
        Long rentasPorEntregar = rentalRepository.countByStatus(RentalStatus.CREATED);

        // 2.- Contar rentas por recoger (status = DELIVERED)
        Long rentasPorRecoger = rentalRepository.countByStatus(RentalStatus.DELIVERED);

        // 3.- Calcular ingresos del mes actual
        // Obtenemos el primer dia del mes actual a las 00:00:00
        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();

        // Obtenemos el primer dia del siguiente mes (para el rango exclusivo)
        LocalDateTime finMes = mesActual.plusMonths(1).atDay(1).atStartOfDay();

        // Solo contamos rentas activas (Excluimos CANCELLED)
        List<RentalStatus> statusActivos = List.of(
                RentalStatus.CREATED,
                RentalStatus.DELIVERED,
                RentalStatus.PICKED_UP
        );

        Long ingresosDelMes = rentalRepository.sumTotalByDateRangeAndStatuses(
                inicioMes,
                finMes,
                statusActivos
        );

        // 4.- Construir y retornar la respuesta
        return new DashboardStatsResponse(rentasPorEntregar, rentasPorRecoger, ingresosDelMes);
    }
}

/*
* Explicación:
* @Service - Marca la clase como un servicio de Spring
* @Transactional(readOnly = true) - Optimiza la consulta ya que solo leemos datos
* YearMonth.now() - Obtiene el año y el mes actual
* atDay(1).atStartOfDay() - Primer día del mes a medianoche
* List.of(...) - Lista inmutable de status activos (excluye CANCELLED
* Siguiente paso: EL CONTROLLER
)*/

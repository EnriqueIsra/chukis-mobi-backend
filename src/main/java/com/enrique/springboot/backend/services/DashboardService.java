package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.DailyIncomeDTO;
import com.enrique.springboot.backend.dto.DashboardStatsResponse;
import com.enrique.springboot.backend.dto.RentalDetailDTO;

import java.util.List;

/**
 * Interface del servicio de Dashboard
 * Define el contrato para obtener estadísticas del sistema
 */
public interface DashboardService {

    /**
     * Obtiene las estadísticas para el dashboard
     * - Rentas por entregar (status CREATED)
     * - Rentas por recoger (status DELIVERED)
     * - Ingresos del mes actual
     *
     * @return DashboardStatsResponse con las métricas
     */
    DashboardStatsResponse getStats();

    // Obtiene las rentas que se deben entregar en un día específico.
    // El parámetro "daysFromToday" indica cuántos días a futuro:
    // 0 = hoy, 1 = mañana
    // Retorna una lista de RentalDetailDTO con resumen de productos y pagos.
    List<RentalDetailDTO> getDeliveries(int daysFromToday);

    // Obtiene los ingresos diarios del mes actual
    // Retorna una lista de DailyIncomeDTO, uno por cada día del mes que tenga al menos una renta
    // Cada elemento tiene: cobrado, anticipos y porCobrar.
    List<DailyIncomeDTO> getMonthlyIncome();

    List<DailyIncomeDTO> getMonthlyIncome(int year, int month);

    // Obtener todas las rentas pendientes de entregar (status CREATED)
    // Retorna List<RentalDetailDTO> con info del cliente, productos, pagos
    // Se usa en el dropdown expandible de la StatCard de Rentas por Entregar del dashboard.
    List<RentalDetailDTO> getPendingRentals();

    // Obtener todas las rentas pendientes de recoger (status DELIVERED)
    // Retorna List<RentalDetailDTO> con info del cliente, productos, pagos
    // Se usa en el dropdown expandible de la StatCard de Rentas por Recoger del dashboard.
    List<RentalDetailDTO> getDeliveredRentals();
}

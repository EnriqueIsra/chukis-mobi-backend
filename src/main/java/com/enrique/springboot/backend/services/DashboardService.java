package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.DashboardStatsResponse;

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
}

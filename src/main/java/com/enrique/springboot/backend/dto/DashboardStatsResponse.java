package com.enrique.springboot.backend.dto;
/**
 * DTo para las estadisticas del dashboard
 * Contiene métricas de rentas e ingresos
 */

public class DashboardStatsResponse {
    private Long rentasPorEntregar;     // Rentas con status CREATED
    private Long rentasPorRecoger;      // Rentas con status DELIVERED
    private Long ingresosDelMes;        // Suma de totales del mes actual

    // Constructor vacío
    public DashboardStatsResponse() {
    }

    // Constructor con parámetros

    public DashboardStatsResponse(Long rentasPorEntregar, Long rentasPorRecoger, Long ingresosDelMes) {
        this.rentasPorEntregar = rentasPorEntregar;
        this.rentasPorRecoger = rentasPorRecoger;
        this.ingresosDelMes = ingresosDelMes;
    }

    // Getters y Setters


    public Long getRentasPorEntregar() {
        return rentasPorEntregar;
    }

    public void setRentasPorEntregar(Long rentasPorEntregar) {
        this.rentasPorEntregar = rentasPorEntregar;
    }

    public Long getRentasPorRecoger() {
        return rentasPorRecoger;
    }

    public void setRentasPorRecoger(Long rentasPorRecoger) {
        this.rentasPorRecoger = rentasPorRecoger;
    }

    public Long getIngresosDelMes() {
        return ingresosDelMes;
    }

    public void setIngresosDelMes(Long ingresosDelMes) {
        this.ingresosDelMes = ingresosDelMes;
    }
}

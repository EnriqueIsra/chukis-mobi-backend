package com.enrique.springboot.backend.dto;

import java.time.LocalDate;

// DTO que representa los ingresos de UN solo dìa
// Se usa para la gráfica de barras apiladas del dashboard
// Cada barra del gráfico tiene 3 segmentos:
// - cobrado: rentas donde ya se pagó el 100%
// - anticipos: dinero recibido de rentas que aún no se pagan completo
// - porCobrar: dinero que falta cobrar de esas rentas
public class DailyIncomeDTO {

    // La fecha del día que representa esta barra.
    // Ejemplo: 2026-02-12
    // En el frontend se mostrará como "12 Feb" o  "Lun 12"
    private LocalDate date;

    // Dinero de rentas 100% pagadas en este día
    // Una renta está "cobrada" cuando totalPaid >= total
    // Ejemplo: Renta de $500, pagó $500 -> cobrado = $500
    private Long cobrado;

    // Suma de pagos parciales de rentas NO completamente pagadas.
    // Son los anticipos que ya se recibieron pero la renta aún debe.
    // Ejemplo: Renta de $500, pagó $200 -> anticipos = $200
    private Long anticipos;

    // Dinero que falta por cobrar de las rentas de este día.
    // Es la diferencia entre el total y lo pagado.
    // Ejemplo: Renta de $500, pagó $200 -> porCobrar = $300
    private Long porCobrar;

    // Constructor vacío requerido por Spring
    public DailyIncomeDTO() {
    }

    // Constructor completo para crear el DTO desde el service
    public DailyIncomeDTO(LocalDate date, Long cobrado, Long anticipos, Long porCobrar) {
        this.date = date;
        this.cobrado = cobrado;
        this.anticipos = anticipos;
        this.porCobrar = porCobrar;
    }

    // Getters y Setters

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getCobrado() {
        return cobrado;
    }

    public void setCobrado(Long cobrado) {
        this.cobrado = cobrado;
    }

    public Long getAnticipos() {
        return anticipos;
    }

    public void setAnticipos(Long anticipos) {
        this.anticipos = anticipos;
    }

    public Long getPorCobrar() {
        return porCobrar;
    }

    public void setPorCobrar(Long porCobrar) {
        this.porCobrar = porCobrar;
    }
}

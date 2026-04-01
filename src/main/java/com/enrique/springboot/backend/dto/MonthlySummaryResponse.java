package com.enrique.springboot.backend.dto;

public class MonthlySummaryResponse {

    private final int year;
    private final int month;
    private final Long totalIncome;         // Pagos recibidos de rentas
    private final Long totalExpenses;       // Gastos operativos
    private final Long totalPayroll;        // Nomina (pagos a trabajadores)
    private final Long totalSubcontracted;  // Costos de mobiliario externo
    private final Long totalOutcome;        // Suma de los 3 egresos
    private final Long netProfit;           // Ingresos - Egresos

    public MonthlySummaryResponse(int year, int month, Long totalIncome,
                                  Long totalExpenses, Long totalPayroll,
                                  Long totalSubcontracted) {
        this.year = year;
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.totalPayroll = totalPayroll;
        this.totalSubcontracted = totalSubcontracted;
        this.totalOutcome = totalExpenses + totalPayroll + totalSubcontracted;
        this.netProfit = totalIncome - this.totalOutcome;
    }

    public int getYear() { return year; }
    public int getMonth() { return month; }
    public Long getTotalIncome() { return totalIncome; }
    public Long getTotalExpenses() { return totalExpenses; }
    public Long getTotalPayroll() { return totalPayroll; }
    public Long getTotalSubcontracted() { return totalSubcontracted; }
    public Long getTotalOutcome() { return totalOutcome; }
    public Long getNetProfit() { return netProfit; }
}

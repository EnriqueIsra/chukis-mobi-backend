package com.enrique.springboot.backend.dto;

import java.util.List;

public class RentalProfitabilityResponse {
    private final Long rentalId;
    private final Long rentalTotal;         // Lo que cobró al cliente
    private final Long totalExternalCost;   // Lo que le costó a proveedores
    private final Long netProfit;           // rentalTotal - totalExternalCost
    private final List<RentalExternalItemResponse> items;

    public RentalProfitabilityResponse(
            Long rentalId,
            Long rentalTotal,
            Long totalExternalCost,
            Long netProfit,
            List<RentalExternalItemResponse> items) {
        this.rentalId = rentalId;
        this.rentalTotal = rentalTotal;
        this.totalExternalCost = totalExternalCost;
        this.netProfit = netProfit;
        this.items = items;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public Long getRentalTotal() {
        return rentalTotal;
    }

    public Long getTotalExternalCost() {
        return totalExternalCost;
    }

    public Long getNetProfit() {
        return netProfit;
    }

    public List<RentalExternalItemResponse> getItems() {
        return items;
    }
}

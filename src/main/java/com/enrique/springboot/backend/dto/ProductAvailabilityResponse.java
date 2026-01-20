package com.enrique.springboot.backend.dto;

public class ProductAvailabilityResponse {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private String color;
    private Long totalStock;
    private Long rentedQuantity;
    private Long availableStock;
    private String imageUrl;

    public ProductAvailabilityResponse(Long id, String name, String description, Long price,
                                        String color, Long totalStock, Long rentedQuantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.color = color;
        this.totalStock = totalStock;
        this.rentedQuantity = rentedQuantity;
        this.availableStock = totalStock - rentedQuantity;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getPrice() { return price; }
    public String getColor() { return color; }
    public Long getTotalStock() { return totalStock; }
    public Long getRentedQuantity() { return rentedQuantity; }
    public Long getAvailableStock() { return availableStock; }
    public String getImageUrl() { return imageUrl; }
}

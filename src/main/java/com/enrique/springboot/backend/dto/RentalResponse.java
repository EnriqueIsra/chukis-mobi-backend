package com.enrique.springboot.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class RentalResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long total;
    private String address;
    private ClientInfo client;
    private UserInfo user;
    private List<RentalItemResponse> items;

    // Constructor completo
    public RentalResponse(Long id, LocalDate startDate, LocalDate endDate, String status,
                          Long total, String address, ClientInfo client, UserInfo user, List<RentalItemResponse> items) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.total = total;
        this.address = address;
        this.client = client;
        this.user = user;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public Long getTotal() {
        return total;
    }

    public String getAddress() {
        return address;
    }

    public ClientInfo getClient() {
        return client;
    }

    public UserInfo getUser() {
        return user;
    }

    public List<RentalItemResponse> getItems() {
        return items;
    }

    // DTO interno para cliente
    public static class ClientInfo {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private String address;

        public ClientInfo(Long id, String name, String phone, String email, String address) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
    }

    // DTO interno para usuario
    public static class UserInfo {
        private Long id;
        private String username;

        public UserInfo(Long id, String username) {
            this.id = id;
            this.username = username;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
    }

    // DTO interno para items
    public static class RentalItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Long unitPrice;
        private Long subtotal;

        public RentalItemResponse(Long productId, String productName, Integer quantity, Long unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = (long) quantity * unitPrice;
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public Integer getQuantity() { return quantity; }
        public Long getUnitPrice() { return unitPrice; }
        public Long getSubtotal() { return subtotal; }
    }
}

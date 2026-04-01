package com.enrique.springboot.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RentalResponse {

    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Long total;
    private String address;
    private ClientInfo client;
    private UserInfo user;
    private List<RentalItemResponse> items;
    private Boolean active;
    private String desactivationReason;
    private String desactivatedByUsername;
    private LocalDateTime desactivationDate;
    private Boolean hasSubcontract;
    private Boolean hasContract;
    private LocalDateTime contractDate;

    public RentalResponse(
            Long id,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String status,
            Long total,
            String address,
            ClientInfo client,
            UserInfo user,
            List<RentalItemResponse> items,
            Boolean active,
            String desactivationReason,
            String desactivatedByUsername,
            LocalDateTime desactivationDate,
            Boolean hasSubcontract,
            Boolean hasContract,
            LocalDateTime contractDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.total = total;
        this.address = address;
        this.client = client;
        this.user = user;
        this.items = items;
        this.active = active;
        this.desactivationReason = desactivationReason;
        this.desactivatedByUsername = desactivatedByUsername;
        this.desactivationDate = desactivationDate;
        this.hasSubcontract = hasSubcontract;
        this.hasContract = hasContract;
        this.contractDate = contractDate;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
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

    public Boolean getActive() { return active; }
    public String getDesactivationReason() { return desactivationReason; }
    public String getDesactivatedByUsername() { return desactivatedByUsername; }
    public LocalDateTime getDesactivationDate() { return desactivationDate; }

    public Boolean getHasSubcontract() {
        return hasSubcontract;
    }

    public Boolean getHasContract() { return hasContract; }
    public LocalDateTime getContractDate() { return contractDate; }

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
        private String phone;

        public UserInfo(Long id, String username, String phone) {
            this.id = id;
            this.username = username;
            this.phone = phone;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getPhone() { return phone; }
    }

    // DTO interno para items
    public static class RentalItemResponse {
        private Long id;            // id del RentalItem (lo necesitamos para el módulo de subcontratados)
        private Integer subcontractedQuantity;
        private Long productId;
        private String productName;
        private Integer quantity;
        private Long unitPrice;
        private Long subtotal;

        public RentalItemResponse(Long id, Integer subcontractedQuantity, Long productId, String productName, Integer quantity, Long unitPrice) {
            this.id = id;
            this.subcontractedQuantity = subcontractedQuantity;
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = (long) quantity * unitPrice;
        }

        public Long getId() {
            return id;
        }

        public Integer getSubcontractedQuantity() {
            return subcontractedQuantity;
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public Integer getQuantity() { return quantity; }
        public Long getUnitPrice() { return unitPrice; }
        public Long getSubtotal() { return subtotal; }
    }
}

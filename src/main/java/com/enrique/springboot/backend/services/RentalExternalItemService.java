package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.RentalExternalItemResponse;
import com.enrique.springboot.backend.dto.RentalProfitabilityResponse;
import com.enrique.springboot.backend.entities.RentalExternalItem;

import java.util.List;
import java.util.Optional;

public interface RentalExternalItemService {
    List<RentalExternalItemResponse> findAllActive();
    List<RentalExternalItemResponse> findAllInactive();
    List<RentalExternalItemResponse> findByRentalId(Long rentalId);
    RentalProfitabilityResponse getProfitabilityByRental(Long rentalId);
    Optional<RentalExternalItem> findById(Long id);
    RentalExternalItem save(RentalExternalItem item);
    RentalExternalItemResponse deactivate(Long id, String reason, Long desactivatedByUserId);
    RentalExternalItemResponse activate(Long id);


}

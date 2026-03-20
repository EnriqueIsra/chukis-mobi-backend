package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.ProviderResponse;
import com.enrique.springboot.backend.entities.Provider;

import java.util.List;
import java.util.Optional;

public interface ProviderService {
    List<ProviderResponse> findAllActive();
    List<ProviderResponse> findAllInactive();
    Optional<Provider> findById(Long id);
    Provider save(Provider provider);
    ProviderResponse deactivate(Long id, String reason, Long desactivatedByUserId);
    ProviderResponse activate(Long id);
}

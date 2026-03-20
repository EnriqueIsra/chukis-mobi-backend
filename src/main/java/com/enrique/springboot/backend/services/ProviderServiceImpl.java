package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.ProviderResponse;
import com.enrique.springboot.backend.entities.Provider;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.repositories.ProviderRepository;
import com.enrique.springboot.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    public ProviderServiceImpl(ProviderRepository providerRepository, UserRepository userRepository) {
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderResponse> findAllActive() {
        return providerRepository.findByActiveTrueOrderByNameAsc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderResponse> findAllInactive() {
        return providerRepository.findByActiveFalseOrderByNameAsc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Provider> findById(Long id) {
        return providerRepository.findById(id);
    }

    @Override
    @Transactional
    public Provider save(Provider provider) {
        return providerRepository.save(provider);
    }

    @Override
    @Transactional
    public ProviderResponse deactivate(Long id, String reason, Long desactivatedByUserId) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado: " + id));

        provider.setActive(false);
        provider.setDesactivationReason(reason);
        provider.setDesactivationDate(LocalDateTime.now());

        Optional<User> optionalUser = userRepository.findById(desactivatedByUserId);
        optionalUser.ifPresent(provider::setDesactivatedBy);

        return toResponse(providerRepository.save(provider));
    }

    @Override
    @Transactional
    public ProviderResponse activate(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado: " + id));

        provider.setActive(true);
        provider.setDesactivationReason(null);
        provider.setDesactivationDate(null);
        provider.setDesactivatedBy(null);
        return toResponse(providerRepository.save(provider));
    }

    // Convierte entidad a DTO de respuesta
    private ProviderResponse toResponse(Provider provider) {
        return new ProviderResponse(
                provider.getId(),
                provider.getName(),
                provider.getPhone(),
                provider.getNotes(),
                provider.getActive(),
                provider.getDesactivationReason(),
                provider.getDesactivatedBy() != null ? provider.getDesactivatedBy().getUsername() : null,
                provider.getDesactivationDate()
        );
    }
}

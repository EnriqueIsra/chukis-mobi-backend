package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.RentalExternalItemResponse;
import com.enrique.springboot.backend.dto.RentalProfitabilityResponse;
import com.enrique.springboot.backend.entities.Provider;
import com.enrique.springboot.backend.entities.RentalExternalItem;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.repositories.RentalExternalItemRepository;
import com.enrique.springboot.backend.repositories.RentalRepository;
import com.enrique.springboot.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RentalExternalItemServiceImpl implements RentalExternalItemService {

    private final RentalExternalItemRepository itemRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public RentalExternalItemServiceImpl(
            RentalExternalItemRepository itemRepository,
            RentalRepository rentalRepository,
            UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalExternalItemResponse> findAllActive() {
        return itemRepository.findByActiveTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalExternalItemResponse> findAllInactive() {
        return itemRepository.findByActiveFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalExternalItemResponse> findByRentalId(Long rentalId) {
        return itemRepository.findByActiveTrueAndRentalId(rentalId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RentalProfitabilityResponse getProfitabilityByRental(Long rentalId) {
        Long rentalTotal = rentalRepository.findById(rentalId)
                .map(r -> r.getTotal())
                .orElse(0L);

        Long totalExternalCost = itemRepository.sumActiveTotalCostByRentalId(rentalId);
        List<RentalExternalItemResponse> items = findByRentalId(rentalId);
        return new RentalProfitabilityResponse(
                rentalId,
                rentalTotal,
                totalExternalCost,
                rentalTotal - totalExternalCost,
                items
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RentalExternalItem> findById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    @Transactional
    public RentalExternalItem save(RentalExternalItem item) {
        return itemRepository.save(item);
    }

    @Override
    public RentalExternalItemResponse deactivate(Long id, String reason, Long desactivatedByUserId) {
        RentalExternalItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + id));

        item.setActive(false);
        item.setDesactivationReason(reason);
        item.setDesactivationDate(LocalDateTime.now());

        Optional<User> optionalUser = userRepository.findById(desactivatedByUserId);
        optionalUser.ifPresent(item::setDesactivatedBy);

        return toResponse(itemRepository.save(item));
    }

    @Override
    public RentalExternalItemResponse activate(Long id) {
        RentalExternalItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + id));

        item.setActive(true);
        item.setDesactivationReason(null);
        item.setDesactivationDate(null);
        item.setDesactivatedBy(null);
        return toResponse(itemRepository.save(item));
    }

    // Convierte entidad a DTO de respuesta
    private RentalExternalItemResponse toResponse(RentalExternalItem item) {
        return new RentalExternalItemResponse(
                item.getId(),
                item.getRental().getId(),
                item.getProvider().getId(),
                item.getProvider().getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitCost(),
                item.getTotalCost(),
                item.getNotes(),
                item.getActive(),
                item.getDesactivationReason(),
                item.getDesactivatedBy() != null ? item.getDesactivatedBy().getUsername() : null,
                item.getDesactivationDate()
        );
    }
}

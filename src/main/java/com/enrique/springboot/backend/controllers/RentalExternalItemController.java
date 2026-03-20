package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreateRentalExternalItemRequest;
import com.enrique.springboot.backend.dto.RentalExternalItemResponse;
import com.enrique.springboot.backend.dto.RentalProfitabilityResponse;
import com.enrique.springboot.backend.entities.RentalExternalItem;
import com.enrique.springboot.backend.repositories.ProviderRepository;
import com.enrique.springboot.backend.repositories.RentalRepository;
import com.enrique.springboot.backend.services.RentalExternalItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rental-external-items")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200/"})
public class RentalExternalItemController {

    private final RentalExternalItemService itemService;
    private final RentalRepository rentalRepository;
    private final ProviderRepository providerRepository;

    public RentalExternalItemController(
            RentalExternalItemService itemService,
            RentalRepository rentalRepository,
            ProviderRepository providerRepository) {
        this.itemService = itemService;
        this.rentalRepository = rentalRepository;
        this.providerRepository = providerRepository;
    }

    @GetMapping
    public ResponseEntity<List<RentalExternalItemResponse>> findAll() {
        return ResponseEntity.ok(itemService.findAllActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<RentalExternalItemResponse>> findInactive() {
        return ResponseEntity.ok(itemService.findAllInactive());
    }

    // ítems externos de una renta específica
    @GetMapping("/by-rental/{rentalId}")
    public ResponseEntity<List<RentalExternalItemResponse>> findByRental (@PathVariable Long rentalId) {
        return ResponseEntity.ok(itemService.findByRentalId(rentalId));
    }

    // Rentabilidad de una renta (ingreso - costos externos)
    @GetMapping("/profitability/{rentalId}")
    public ResponseEntity<RentalProfitabilityResponse> getProfitability(@PathVariable Long rentalId) {
        return ResponseEntity.ok(itemService.getProfitabilityByRental(rentalId));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateRentalExternalItemRequest request) {
        RentalExternalItem item = new RentalExternalItem();

        var rental = rentalRepository.findById(request.getRentalId());
        if (rental.isEmpty()) {
            return ResponseEntity.badRequest().body("Renta no encontrada");
        }
        item.setRental(rental.get());

        var provider = providerRepository.findById(request.getProviderId());
        if (provider.isEmpty()){
            return ResponseEntity.badRequest().body("Proveedor no encontrado");
        }
        item.setProvider(provider.get());

        item.setDescription(request.getDescription());
        item.setQuantity(request.getQuantity());
        item.setUnitCost(request.getUnitCost());
        // totalCost se calcula aquí
        item.setTotalCost((long) request.getQuantity() * request.getUnitCost());
        item.setNotes(request.getNotes());

        RentalExternalItem saved = itemService.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody CreateRentalExternalItemRequest request) {
        Optional<RentalExternalItem> optionalRentalExternalItem = itemService.findById(id);
        if (optionalRentalExternalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        RentalExternalItem item = optionalRentalExternalItem.get();

        var provider = providerRepository.findById(request.getProviderId());
        if (provider.isEmpty()){
            return ResponseEntity.badRequest().body("Proveedor no encontrado");
        }
        item.setProvider(provider.get());

        item.setDescription(request.getDescription());
        item.setQuantity(request.getQuantity());
        item.setUnitCost(request.getUnitCost());
        item.setTotalCost((long) request.getQuantity() * request.getUnitCost());
        item.setNotes(request.getNotes());

        itemService.save(item);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body) {
        String reason = (String) body.get("reason");
        Long userId = Long.valueOf(body.get("userId").toString());
        return ResponseEntity.ok(itemService.deactivate(id, reason, userId));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.activate(id));
    }
}
























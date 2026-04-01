package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreateRentalExternalItemRequest;
import com.enrique.springboot.backend.dto.RentalExternalItemResponse;
import com.enrique.springboot.backend.dto.RentalProfitabilityResponse;
import com.enrique.springboot.backend.entities.RentalExternalItem;
import com.enrique.springboot.backend.entities.RentalItem;
import com.enrique.springboot.backend.repositories.ProviderRepository;
import com.enrique.springboot.backend.repositories.RentalExternalItemRepository;
import com.enrique.springboot.backend.repositories.RentalItemRepository;
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
    private final RentalItemRepository rentalItemRepository;
    private final ProviderRepository providerRepository;
    private final RentalExternalItemRepository externalItemRepository;

    public RentalExternalItemController(
            RentalExternalItemService itemService,
            RentalItemRepository rentalItemRepository,
            ProviderRepository providerRepository,
            RentalExternalItemRepository externalItemRepository) {
        this.itemService = itemService;
        this.rentalItemRepository = rentalItemRepository;
        this.providerRepository = providerRepository;
        this.externalItemRepository = externalItemRepository;
    }

    // Todos los items activos
    @GetMapping
    public ResponseEntity<List<RentalExternalItemResponse>> findAllActive() {
        return ResponseEntity.ok(itemService.findAllActive());
    }

    // Todos los items inactivos
    @GetMapping("/inactive")
    public ResponseEntity<List<RentalExternalItemResponse>> findAllInactive() {
        return ResponseEntity.ok(itemService.findAllInactive());
    }

    // Items de una renta específica
    @GetMapping("/by-rental/{rentalId}")
    public ResponseEntity<List<RentalExternalItemResponse>> findByRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(itemService.findByRentalId(rentalId));
    }

    // Rentabilidad de una renta
    @GetMapping("/profitability/{rentalId}")
    public ResponseEntity<RentalProfitabilityResponse> getProfitability(@PathVariable Long rentalId) {
        return ResponseEntity.ok(itemService.getProfitabilityByRental(rentalId));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateRentalExternalItemRequest request) {
        // Buscar el RentalItem
        Optional<RentalItem> rentalItemOpt = rentalItemRepository.findById(request.getRentalItemId());
        if (rentalItemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Item de renta no encontrado");
        }
        RentalItem rentalItem = rentalItemOpt.get();

        // Validar que no exceda la cantidad subcontratada
        Long alreadyAssigned = externalItemRepository.sumActiveQuantityByRentalItemId(rentalItem.getId());
        long available = rentalItem.getSubcontractedQuantity() - alreadyAssigned;
        if (request.getQuantity() > available) {
            return ResponseEntity.badRequest().body("Solo quedan " + available + " unidades por asignar");
        }

        // Buscar proveedor
        var provider = providerRepository.findById(request.getProviderId());
        if (provider.isEmpty()) {
            return ResponseEntity.badRequest().body("Proveedor no encontrado");
        }

        RentalExternalItem item = new RentalExternalItem();
        item.setRentalItem(rentalItem);
        item.setProvider(provider.get());
        item.setQuantity(request.getQuantity());
        item.setUnitCost(request.getUnitCost());
        item.setTotalCost((long) request.getQuantity() * request.getUnitCost());
        item.setNotes(request.getNotes());

        RentalExternalItem saved = itemService.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody CreateRentalExternalItemRequest request) {
        Optional<RentalExternalItem> optional = itemService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        RentalExternalItem item = optional.get();

        var provider = providerRepository.findById(request.getProviderId());
        if (provider.isEmpty()) {
            return ResponseEntity.badRequest().body("Proveedor no encontrado");
        }
        item.setProvider(provider.get());
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

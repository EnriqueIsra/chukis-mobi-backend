package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreateRentalRequest;
import com.enrique.springboot.backend.dto.RentalResponse;
import com.enrique.springboot.backend.dto.UpdateRentalStatusRequest;
import com.enrique.springboot.backend.entities.Client;
import com.enrique.springboot.backend.entities.Rental;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.services.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200"})
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // --------------------
    // LISTAR RENTAS
    // --------------------
    @GetMapping
    public List<RentalResponse> findAll() {
        return rentalService.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // --------------------
    // OBTENER RENTA POR ID
    // --------------------
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> findById(@PathVariable Long id) {
        Optional<Rental> optionalRental = rentalService.findById(id);
        if (optionalRental.isPresent()) {
            return ResponseEntity.ok(mapToResponse(optionalRental.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // --------------------
    // CREAR RENTA
    // --------------------
    @PostMapping
    public ResponseEntity<RentalResponse> create(@Valid @RequestBody CreateRentalRequest request) {
        Rental rental = rentalService.createRentalFromDto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(rental));
    }

    // --------------------
    // ACTUALIZAR RENTA
    // --------------------
    @PutMapping("/{id}")
    public ResponseEntity<RentalResponse> update(@PathVariable Long id, @Valid @RequestBody CreateRentalRequest request) {
        try {
            Rental rental = rentalService.updateRentalFromDto(id, request);
            return ResponseEntity.ok(mapToResponse(rental));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --------------------
    // ELIMINAR RENTA
    // --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<RentalResponse> delete(@PathVariable Long id) {
        Optional<Rental> optionalRental = rentalService.deleteById(id);
        if (optionalRental.isPresent()) {
            return ResponseEntity.ok(mapToResponse(optionalRental.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // --------------------
    // HELPER: Mapear Rental a RentalResponse
    // --------------------
    private RentalResponse mapToResponse(Rental rental) {
        Client client = rental.getClient();
        RentalResponse.ClientInfo clientInfo = new RentalResponse.ClientInfo(
                client.getId(),
                client.getNombre(),
                client.getTelefono(),
                client.getEmail(),
                client.getDireccion()
        );

        User user = rental.getUser();
        RentalResponse.UserInfo userInfo = new RentalResponse.UserInfo(
                user.getId(),
                user.getUsername()
        );

        List<RentalResponse.RentalItemResponse> items = rental.getItems().stream()
                .map(item -> new RentalResponse.RentalItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new RentalResponse(
                rental.getId(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getStatus().name(),
                rental.getTotal(),
                rental.getAddress(),
                clientInfo,
                userInfo,
                items
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RentalResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        try {
            Rental rental = rentalService.updateStatus(id, status);
            return ResponseEntity.ok(mapToResponse(rental));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

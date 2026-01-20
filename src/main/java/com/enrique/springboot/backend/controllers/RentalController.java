package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreateRentalRequest;
import com.enrique.springboot.backend.dto.RentalResponse;
import com.enrique.springboot.backend.entities.Rental;
import com.enrique.springboot.backend.services.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                .map(rental -> new RentalResponse(
                        rental.getId(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getStatus().name(),
                        rental.getTotal()
                ))
                .toList();
    }

    // --------------------
    // CREAR RENTA
    // --------------------
    @PostMapping
    public ResponseEntity<RentalResponse> create(@Valid @RequestBody CreateRentalRequest request) {

        Rental rental = rentalService.createRentalFromDto(request);

        RentalResponse response = new RentalResponse(
                rental.getId(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getStatus().name(),
                rental.getTotal()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

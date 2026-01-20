package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.CreateRentalRequest;
import com.enrique.springboot.backend.entities.Rental;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalService {
    List<Rental> findAll();

    Optional<Rental> findById(Long id);
    Rental createRental(
            Rental rental,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Rental> deleteById(Long id);

    Rental createRentalFromDto(CreateRentalRequest request);

}

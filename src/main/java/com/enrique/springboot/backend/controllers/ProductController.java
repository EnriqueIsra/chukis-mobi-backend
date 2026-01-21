package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.ProductAvailabilityResponse;
import com.enrique.springboot.backend.entities.Product;
import com.enrique.springboot.backend.enums.RentalStatus;
import com.enrique.springboot.backend.repositories.RentalItemRepository;
import com.enrique.springboot.backend.services.ProductService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200/"})
public class ProductController {

    private final ProductService service;
    private final RentalItemRepository rentalItemRepository;

    public ProductController(ProductService service, RentalItemRepository rentalItemRepository) {
        this.service = service;
        this.rentalItemRepository = rentalItemRepository;
    }

    @GetMapping
    public ResponseEntity<List<Product>> list(){
        return ResponseEntity.ok(service.findAll());
    }

    // --------------------
    // DISPONIBILIDAD POR FECHAS Y HORAS
    // --------------------
    @GetMapping("/availability")
    public ResponseEntity<List<ProductAvailabilityResponse>> getAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<RentalStatus> activeStatuses = List.of(RentalStatus.CREATED, RentalStatus.DELIVERED);

        List<ProductAvailabilityResponse> availability = service.findAll().stream()
                .map(product -> {
                    Long rentedQuantity = rentalItemRepository.getRentedQuantityByProductAndDates(
                            product.getId(),
                            startDate,
                            endDate,
                            activeStatuses
                    );
                    return new ProductAvailabilityResponse(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            product.getColor(),
                            product.getStock(),
                            rentedQuantity,
                            product.getImageUrl()
                    );
                })
                .toList();

        return ResponseEntity.ok(availability);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> details (@PathVariable Long id){
        Optional<Product> optionalProduct = service.findById(id);
        if (optionalProduct.isPresent()){
            return ResponseEntity.ok(optionalProduct.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> create (@RequestBody Product product){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update (@RequestBody Product product, @PathVariable Long id){
        Optional<Product> optionalProduct = service.findById(id);
        if (optionalProduct.isPresent()){
            Product productDb = optionalProduct.orElseThrow();
            productDb.setName(product.getName());
            productDb.setDescription(product.getDescription());
            productDb.setPrice(product.getPrice());
            productDb.setColor(product.getColor());
            productDb.setStock(product.getStock());
            productDb.setImageUrl(product.getImageUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(productDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> delete (@PathVariable Long id){
        Optional<Product> optionalProduct = service.deleteById(id);
        if (optionalProduct.isPresent()){
            Product productDeleted = optionalProduct.orElseThrow();
            return ResponseEntity.status(HttpStatus.OK).body(productDeleted);
        }
        return ResponseEntity.notFound().build();
    }
}

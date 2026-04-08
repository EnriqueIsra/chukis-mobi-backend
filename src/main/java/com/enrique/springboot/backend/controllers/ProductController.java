package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.ProductAvailabilityResponse;
import com.enrique.springboot.backend.entities.Product;
import com.enrique.springboot.backend.entities.ProductImage;
import com.enrique.springboot.backend.enums.RentalStatus;
import com.enrique.springboot.backend.repositories.ProductImageRepository;
import com.enrique.springboot.backend.repositories.RentalItemRepository;
import com.enrique.springboot.backend.services.ProductService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200/"})
public class ProductController {

    private final ProductService productService;
    private final RentalItemRepository rentalItemRepository;
    private final ProductImageRepository productImageRepository;

    public ProductController(
            ProductService productService,
            RentalItemRepository rentalItemRepository,
            ProductImageRepository productImageRepository) {
        this.productService = productService;
        this.rentalItemRepository = rentalItemRepository;
        this.productImageRepository = productImageRepository;
    }

    @GetMapping
    public ResponseEntity<List<Product>> list(){
        return ResponseEntity.ok(productService.findAllActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Product>> listInactive(){
        return ResponseEntity.ok(productService.findAllInactive());
    }

    // --------------------
    // DISPONIBILIDAD POR FECHAS Y HORAS
    // --------------------
    @GetMapping("/availability")
    public ResponseEntity<List<ProductAvailabilityResponse>> getAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<RentalStatus> activeStatuses = List.of(RentalStatus.CREATED, RentalStatus.DELIVERED);

        List<ProductAvailabilityResponse> availability = productService.findAll().stream()
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
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()){
            return ResponseEntity.ok(optionalProduct.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> create (@RequestBody Product product){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update (@RequestBody Product product, @PathVariable Long id){
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()){
            Product productDb = optionalProduct.orElseThrow();
            productDb.setName(product.getName());
            productDb.setDescription(product.getDescription());
            productDb.setPrice(product.getPrice());
            productDb.setColor(product.getColor());
            productDb.setStock(product.getStock());
            productDb.setImageUrl(product.getImageUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(productDb));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body) {
        String reason = (String) body.get("reason");
        Long userId = Long.valueOf(body.get("userId").toString());
        return ResponseEntity.ok(productService.deactivate(id, reason, userId));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        return ResponseEntity.ok(productService.activate(id));
    }

    // === GALERÍA DE IMÁGENES ===

    // Obtener todas las imágenes de un producto
    // Se usa al abrir el modal de detalles para cargar la galería
    @GetMapping("/{id}/images")
    public ResponseEntity<?> getImages(@PathVariable Long id) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(id);
        // Mapeamos una lista simple, con id, imageUrl y displayOrder
        // No enviamos el objeto Product completo para evitar referencias circulares
        var response = images.stream().map(img -> Map.of(
                "id", img.getId(),
                "imageUrl", img.getImageUrl(),
                "displayOrder", img.getDisplayOrder()
        )).toList();
        return ResponseEntity.ok(response);
    }

    // Agregar una nueva imagen a la galería de un producto
    // El frontend sube la imagen al servidor (FileController) y nos manda la URL resultante
    @PostMapping("/{id}/images")
    @Transactional
    public ResponseEntity<?> addImage (@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String imageUrl = body.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            return ResponseEntity.badRequest().body("La URL de la imagen es obligatoria");
        }
        
        // El displayOrder se asigna automáticamente al final de la galería
        Long count = productImageRepository.countByProductId(id);

        ProductImage image = new ProductImage();
        image.setProduct(productOpt.get());
        image.setImageUrl(imageUrl);
        image.setDisplayOrder(count.intValue());

        productImageRepository.save(image);
        return ResponseEntity.status(HttpStatus.CREATED).body(image.getId());
    }

    // Eliminar una imagen de la galería
    @DeleteMapping("/images/{imageId}")
    @Transactional
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        Optional<ProductImage> imageOpt = productImageRepository.findById(imageId);
        if (imageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        productImageRepository.delete(imageOpt.get());
        return ResponseEntity.ok().build();
    }
}

package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreateQuotationRequest;
import com.enrique.springboot.backend.dto.QuotationResponse;
import com.enrique.springboot.backend.entities.Quotation;
import com.enrique.springboot.backend.entities.QuotationItem;
import com.enrique.springboot.backend.entities.Product;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.repositories.ProductRepository;
import com.enrique.springboot.backend.repositories.QuotationRepository;
import com.enrique.springboot.backend.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200/"})
@RestController
@RequestMapping("/api/quotations")
public class QuotationController {

    private final QuotationRepository quotationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public QuotationController(QuotationRepository quotationRepository,
                               ProductRepository productRepository,
                               UserRepository userRepository) {
        this.quotationRepository = quotationRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<QuotationResponse>> findAllActive() {
        return ResponseEntity.ok(
                quotationRepository.findByActiveTrueOrderByCreatedAtDesc()
                        .stream().map(this::toResponse).toList()
        );
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<QuotationResponse>> findAllInactive() {
        return ResponseEntity.ok(
                quotationRepository.findByActiveFalseOrderByCreatedAtDesc()
                        .stream().map(this::toResponse).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponse> findById(@PathVariable Long id) {
        return quotationRepository.findById(id)
                .map(q -> ResponseEntity.ok(toResponse(q)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@Valid @RequestBody CreateQuotationRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        Quotation quotation = new Quotation();
        quotation.setCreatedAt(LocalDateTime.now());
        quotation.setNotes(request.getNotes());
        quotation.setCreatedBy(userOpt.get());

        List<QuotationItem> items = new ArrayList<>();
        long total = 0;

        for (CreateQuotationRequest.QuotationItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemReq.getProductId()));

            QuotationItem item = new QuotationItem();
            item.setQuotation(quotation);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());
            items.add(item);

            total += (long) itemReq.getQuantity() * product.getPrice();
        }

        quotation.setItems(items);
        quotation.setTotal(request.getTotal() != null && request.getTotal() > 0 ? request.getTotal() : total);

        Quotation saved = quotationRepository.save(quotation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CreateQuotationRequest request) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotizacion no encontrada"));

        quotation.getItems().clear();
        quotation.setNotes(request.getNotes());

        long total = 0;
        for (CreateQuotationRequest.QuotationItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemReq.getProductId()));

            QuotationItem item = new QuotationItem();
            item.setQuotation(quotation);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());
            quotation.getItems().add(item);

            total += (long) itemReq.getQuantity() * product.getPrice();
        }

        quotation.setTotal(request.getTotal() != null && request.getTotal() > 0 ? request.getTotal() : total);
        quotationRepository.save(quotation);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Transactional
    public ResponseEntity<?> deactivate(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotizacion no encontrada"));

        quotation.setActive(false);
        quotation.setDesactivationReason((String) body.get("reason"));
        quotation.setDesactivationDate(LocalDateTime.now());

        Long userId = Long.valueOf(body.get("userId").toString());
        userRepository.findById(userId).ifPresent(quotation::setDesactivatedBy);

        return ResponseEntity.ok(toResponse(quotationRepository.save(quotation)));
    }

    @PatchMapping("/{id}/activate")
    @Transactional
    public ResponseEntity<?> activate(@PathVariable Long id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotizacion no encontrada"));

        quotation.setActive(true);
        quotation.setDesactivationReason(null);
        quotation.setDesactivationDate(null);
        quotation.setDesactivatedBy(null);

        return ResponseEntity.ok(toResponse(quotationRepository.save(quotation)));
    }

    private QuotationResponse toResponse(Quotation q) {
        List<QuotationResponse.QuotationItemResponse> items = q.getItems().stream()
                .map(item -> new QuotationResponse.QuotationItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getQuantity(),
                        item.getPrice()
                )).toList();

        return new QuotationResponse(
                q.getId(),
                q.getCreatedAt(),
                q.getNotes(),
                q.getTotal(),
                q.getCreatedBy().getUsername(),
                q.getActive(),
                q.getDesactivationReason(),
                q.getDesactivatedBy() != null ? q.getDesactivatedBy().getUsername() : null,
                q.getDesactivationDate(),
                items
        );
    }
}

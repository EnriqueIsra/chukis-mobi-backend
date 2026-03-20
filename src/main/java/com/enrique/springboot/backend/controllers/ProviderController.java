package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreateProviderRequest;
import com.enrique.springboot.backend.dto.ProviderResponse;
import com.enrique.springboot.backend.entities.Provider;
import com.enrique.springboot.backend.services.ProviderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200/"})
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponse>> findAll() {
        return ResponseEntity.ok(providerService.findAllActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<ProviderResponse>> findInactive() {
        return ResponseEntity.ok(providerService.findAllInactive());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateProviderRequest request) {
        Provider provider = new Provider();
        provider.setName((request.getName()));
        provider.setPhone((request.getPhone()));
        provider.setNotes((request.getNotes()));

        Provider saved = providerService.save(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody CreateProviderRequest request) {
        Optional<Provider> optionalProvider = providerService.findById(id);
        if (optionalProvider.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Provider provider = optionalProvider.get();
        provider.setName(request.getName());
        provider.setPhone((request.getPhone()));
        provider.setNotes((request.getNotes()));

        providerService.save(provider);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body) {
        String reason = (String) body.get("reason");
        Long userId = Long.valueOf(body.get("userId").toString());
        return ResponseEntity.ok(providerService.deactivate(id, reason, userId));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.activate(id));
    }
}

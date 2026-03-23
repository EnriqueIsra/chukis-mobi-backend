package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.entities.Client;
import com.enrique.springboot.backend.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200"})
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Client>> list() {
        return ResponseEntity.ok(service.findAllActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Client>> listInactive() {
        return ResponseEntity.ok(service.findAllInactive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> details(@PathVariable Long id) {
        Optional<Client> optionalClient = service.findById(id);
        if (optionalClient.isPresent()) {
            return ResponseEntity.ok(optionalClient.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(client));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@RequestBody Client client, @PathVariable Long id) {
        Optional<Client> optionalClient = service.findById(id);
        if (optionalClient.isPresent()) {
            Client clientDb = optionalClient.orElseThrow();
            clientDb.setNombre(client.getNombre());
            clientDb.setTelefono(client.getTelefono());
            clientDb.setDireccion(client.getDireccion());
            clientDb.setEmail(client.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(clientDb));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body) {
        String reason = (String) body.get("reason");
        Long userId = Long.valueOf(body.get("userId").toString());
        return ResponseEntity.ok(service.deactivate(id, reason, userId));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        return ResponseEntity.ok(service.activate(id));
    }
}

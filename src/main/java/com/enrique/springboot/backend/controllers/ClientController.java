package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.entities.Client;
import com.enrique.springboot.backend.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        return ResponseEntity.ok(service.findAll());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Client> delete(@PathVariable Long id) {
        Optional<Client> optionalClient = service.deleteById(id);
        if (optionalClient.isPresent()) {
            Client clientDeleted = optionalClient.orElseThrow();
            return ResponseEntity.status(HttpStatus.OK).body(clientDeleted);
        }
        return ResponseEntity.notFound().build();
    }
}

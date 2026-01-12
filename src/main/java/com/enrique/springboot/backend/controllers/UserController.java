package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200"})
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // -------------------
    // LISTAR USUARIOS
    // -------------------
    @GetMapping
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    // -------------------
    // DETALLE USUARIO
    // -------------------
    @GetMapping("/{id}")
    public ResponseEntity<User> details(@PathVariable Long id) {
        Optional<User> optionalUser = service.findById(id);
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    // -------------------
    // CREAR USUARIO
    // -------------------
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    // -------------------
    // ACTUALIZAR USUARIO
    // -------------------
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@RequestBody User user, @PathVariable Long id) {

        Optional<User> optionalUser = service.findById(id);

        if (optionalUser.isPresent()) {
            User userDb = optionalUser.orElseThrow();

            userDb.setUsername(user.getUsername());
            userDb.setRole(user.getRole());
            userDb.setImageUrl(user.getImageUrl());

            // ⚠ solo actualizamos password si viene
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                userDb.setPassword(user.getPassword());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(userDb));
        }

        return ResponseEntity.notFound().build();
    }

    // -------------------
    // ELIMINAR USUARIO
    // -------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable Long id) {

        Optional<User> optionalUser = service.deleteById(id);

        if (optionalUser.isPresent()) {
            User userDeleted = optionalUser.orElseThrow();
            return ResponseEntity.ok(userDeleted);
        }

        return ResponseEntity.notFound().build();
    }
}

package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.LoginRequest;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200"})
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Optional<User> userOptional =
                service.login(request.getUsername(), request.getPassword());

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        }

        return ResponseEntity.status(401)
                .body("Usuario o contraseña incorrectos");
    }
}

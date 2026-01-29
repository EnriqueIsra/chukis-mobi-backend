package com.enrique.springboot.backend.controllers;

// DTOs para request y response
import com.enrique.springboot.backend.dto.AuthResponse;
import com.enrique.springboot.backend.dto.LoginRequest;

// Entidad User
import com.enrique.springboot.backend.entities.User;

// Servicios
import com.enrique.springboot.backend.services.JwtService;
import com.enrique.springboot.backend.services.UserService;

// Spring
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Java
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/* Controlador de autenticación
*
* Maneja el login de usuarios y genera tokens JWT */

@RestController
@RequestMapping("/api/auth")
@CrossOrigin({"http://localhost:5173", "http://localhost:4200"})
public class AuthController {

    // ========================
    // Dependencias inyectadas
    // ========================

    // Servicio de usuarios para validar credenciales
    private final UserService service;

    // Servicio JWT para generar tokens
    private final JwtService jwtService;

    // ========================
    // Constructor (Inyección de dependencias)
    // ========================
    public AuthController(UserService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    // ========================
    // Endpoint: POST /api/auth/login
    // ========================
    /* Procesa el login de un usuario
    *
    * @param request - contiene username y password
    * @return AuthResponse con token si es exitoso o error 401 si falla */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // 1.- Intentar autenticar al usuario con sus credenciales
        Optional<User> userOptional = service.login(
                request.getUsername(),
                request.getPassword()
        );

        // 2.- Si el usuario no existe o la contraseña es incorrecta
        if (userOptional.isEmpty()) {
            // Retornar 401 Unauthorized
            return ResponseEntity
                    .status(401)
                    .body("Usuario o contraseña incorrectos");
        }

        // 3.- Si las credenciales son correctas, obtener el usuario
        User user = userOptional.get();

        // 4.- Crear claims adicionales para incluir en el token
        // Estos datos estarán disponibles al decodificar el token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("role", user.getRole().name());

        // 5.- Generar el token JWT
        String token = jwtService.generateToken(user.getUsername(), extraClaims);

        // 6.- Retornar la respuesta con token y datos del usuario
        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getImageUrl(),
                user.getTelefono()
        ));
    }
}

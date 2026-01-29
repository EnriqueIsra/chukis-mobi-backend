package com.enrique.springboot.backend.dto;

/* DTO de respuesta de autenticación
*
* Esta clase representa la respuesta que se enviamos al frontend
* cuando el login es exitoso. Contiene:
* - El token JWT para futuras peticiones
* - Información básica del usuario (sin la contraseña) */

public class AuthResponse {
    // ===========
    // Atributos
    // ===========

    // El token JWT generado para el usuario
    private String token;

    // ID del usuario (útil para el frontend)
    private Long id;

    // Nombre de usuario
    private String username;

    // Rol del usuario (ADMIN, CHAMBEADOR)
    private String role;

    // URL de la imagen de perfil (opcional)
    private String imageUrl;

    // Teléfono del usuario
    private String telefono;

    // ==============================
    // Constructor vacío
    // Necesario para que Jackson pueda serializar/deserializar
    // ==============================
    public AuthResponse() {
    }

    // ==============================
    // Constructor con todos los campos
    // Facilita crear la respuesta en una sola línea
    // ==============================
    public AuthResponse(String token, Long id, String username, String role, String imageUrl, String telefono) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.role = role;
        this.imageUrl = imageUrl;
        this.telefono = telefono;
    }

    // ==============================
    // Getters y Setters
    // Necesarios para que Jackson serialice a JSON
    // ==============================
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

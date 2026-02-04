package com.enrique.springboot.backend.security;

// Configuracion de Spring
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Spring Security
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Para el cifrado de constraseñas
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Para configurar CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/* Configuración de Seguridad de la Aplicación
*
* Esta clase configura:
* 1.- Qué rutas son públicas y cuales requieren autenticación
* 2.- Cómo manejar las sesiones (stateless para JWT)
* 3.- CORS para permitir peticiones desde el frontend
* 4.- El orden de los filtros de seguridad */
@Configuration      // Indica que es una clase de configuración
@EnableWebSecurity  // Habilita la seguridad web de Spring
public class SecurityConfig {

    // Nuestro filtro JWT
    private final JwtAuthenticationFilter jwtAuthFilter;

    // Constructor - Inyección de dependencias

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /* Configura la cadena de filtros de seguridad
     * Este es el corazon de la configuración de seguridad */
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
                // 1.- Configurar CORS (Cross-Origin Resource Sharing)
                // Permite que el frontend (otro origen) haga peticiones al backend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2.- Deshabilitar CSRF (Cross-Site Request Forgery)
                // No necesario con JWT porque usamos tokens en headers, no cookies
                .csrf(AbstractHttpConfigurer::disable)

                // 3.- Configurar manejo de sesiones
                // STATELESS = No guardar sesión en el servidor
                // Cada petición debe traer su propio token JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4.- Configurar autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (no requieren token)
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/files/**").permitAll() // Permite que las imagenes puedan ser accesibles y por lo tanto que sean visibles

                        // Todas las demas rutas requieren autenticación
                        .anyRequest().authenticated()
                )

                // 5.- Agregar nuestro filtro JWT ANTES del filtro de autenticación estándar
                // Esto asegura que el token se valide antes de cualquier otra cosa
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* Configuración de CORS
    * Permite que el frontend en localhost:5173 haga peticiones */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (frontend React)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (incluye Authorization para el token)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Permite enviar cookies/credenciales
        configuration.setAllowCredentials(true);

        // Aplicar esta configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /* Bean para encriptar contraseñas
    * BCrypt es un algoritmo seguro que incluye salt automático */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
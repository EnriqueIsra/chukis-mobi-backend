package com.enrique.springboot.backend.security;

// Servlet para manejar peticiones HTTP
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Spring Security
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Nuestro servicio JWT
import com.enrique.springboot.backend.services.JwtService;

// Java
import java.io.IOException;
import java.util.List;

/* Filtro de autenticación JWT
*
* Este filtro se ejecuta en CADA petición HTTP antes de llegar al controller
* Su trabajo es:
* 1.- Extraer el token del header "Authorization"
* 2.- Validar el token
* 3.- Si es válido, autenticar al usuario en el contexto de Spring Security
*/

@Component // Lo registra como bean de Spring
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Servicio para validar tokens
    private final JwtService jwtService;

    // Constructor - Inyección de dependencias
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /* Método principal del filtro - se ejecuta en cada petición */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1.- Obtener el header "Authorization"
        // El formato esperado es: "Bearer eyJhbGciOiJUzI1NiJ9..."
        final String authHeader = request.getHeader("Authorization");

        // 2.- Si no hay header o no empieza con "Bearer ", continuar sin autenticar
        // (La petición será rechazada después si la ruta requiere autenticación)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3.- Extraer el token (quitar "Bearer " del inicio)
        final String token = authHeader.substring(7);

        try {
           // 4.- Extraer el username del toke
            final String username = jwtService.extractUsername(token);

            // 5.- Si hay username y no hay autenticación previa en el contexto
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6.- Validar que el token sea válido para este usuario
                if (jwtService.isTokenValid(token, username)) {

                    // 7.- Extraer el rol del token
                    String role =  (String) jwtService.extractClaim(token, "role");

                    // 8.- Crear lista de autoridades (roles) para Spring Security
                    // El prefijo "ROLE_" es requerido por Spring Security
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    // 9.- Crear el objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,   // Principal (quién es)
                                    null,       // Credentials (no necesitamos password aquí)
                                    authorities // Roles/permisos
                            );
                    // 10.- Guardar la autenticación en el contexto de Spirng Security
                    // Esto marca al usuario como "autenticado" para esta petición
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si hay cualquier error al validar el token, simplemente no autenticamos
            // La petición continuará y será rechazada si la ruta requiere auth
            System.out.println("Error validando el token: " + e.getMessage());
        }

        // 11.- Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}

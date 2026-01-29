package com.enrique.springboot.backend.services;

// ==================================================
// Imports necesarios para JWT
// ==================================================

// Anotaciones de Spring para la inyección de dependencias
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Clases de la librería JJWT para manejar tokens
import io.jsonwebtoken.Claims;          // Contiene los datos dentro del token
import io.jsonwebtoken.Jwts;            // Clase principal para crear/leer tokens
import io.jsonwebtoken.security.Keys;   // Para generar la clave de la firma

// Clases de seguridad de Java
import javax.crypto.SecretKey;  // Tipo de clave secreta
import java.util.Date;          // Para manejar fechas de expiración
import java.util.Map;           // Para manejar datos adicionales en el Token
import java.util.HashMap;       // Implementación del Map

/*
* Servicio JWT
*
* Esta clase se encarga de:
* 1.- Generar tokens JWT cuando el usuario hace login
* 2.- Validar tokens en cada petición
* 3.- Extraer informacion del usuario desde el token
*/

@Service // Marca esta clase como un servicio de Spring (singleton)
public class JwtService {

    // ==================================================
    // Propiedades inyectadas desde application.properties
    // ==================================================

    // @Value inyecta el valor de la propiedad jwt.secret.key
    @Value("${jwt.secret.key}")
    private String secretKey;

    // @Value inyecta el tiempo de expiración (12 horas en este caso)
    @Value("${jwt.expiration.time}")
    private long expirationTime;

    // ==================================================
    // Método para generar la clave de firma
    // ==================================================

    /*
    * Convierte el string secretKey en un objeto SecretKey
    * que puede usar el algoritmo HMAC-SHA256 para firmar
    */
    private SecretKey getSigningKey() {
        // Convierte el string a bytes y crea una clave HMAC-SHA
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // ==================================================
    // Método para generar token
    // ==================================================

    /*
    * Genera un nuevo token JTW para un usuario
    *
    * @param username - El nombre de usuario (será el "subject" del token)
    * @param extraClaims - Datos adicionales a incluir (id, role, etc.)
    * @return String con el token JWT generado
    */
    public String generateToken(String username, Map<String, Object> extraClaims) {

        // Fecha actual (momento de creación del token)
        Date now = new Date();

        // Fecha de expiración = ahora + tiempo configurado (12 horas)
        Date expirationDate = new Date(now.getTime() + expirationTime);

        // Construye y retorna el token
        return Jwts.builder()
                .claims(extraClaims)        // Agrega datos extra (id, role)
                .subject(username)          // El "dueño" del token
                .issuedAt(now)              // Fecha de creación
                .expiration(expirationDate) // Fecha de expiración
                .signWith(getSigningKey())  // Firma con nuestra clave secreta
                .compact();                 // Convierte todo a String
    }

    /*
    * Versión simplificada: genera token solo con username
    */
    public String generateToken(String username) {
        return generateToken(username, new HashMap<>());
    }

    // ==================================================
    // Métodos para extraer información del token
    // ==================================================

    /*
    * Extrae TODOS los claims (datos) del token
    * Si es invalido o expiró, lanza excepción
    */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()                    // Crea un parser de JWT
                .verifyWith(getSigningKey())    // Usa la clave para verificar
                .build()                        // Construye el parser
                .parseSignedClaims(token)       // Parsea el token
                .getPayload();                  // Obtiene los datos (claims)
    }

    /*
    * Extrae el username (subject) del token
    */
    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }

    /*
    * Extrae la fecha de expiración del token
    */
    public Date extractExpirationDate(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /* Extrae un claim específico por su nombre
    * Útil para obtener el id, role, etc.
    */
    public Object extractClaim(String token, String claimName) {
        return extractAllClaims(token).get(claimName);
    }

    // ==================================================
    // Métodos de validación
    // ==================================================

    /* Verifica si el token ya expiró
    * @retunr true si expiró, false si aún es válido */
    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    /* Valida que el token sea válido para un usuario específico
    *
    * @param token - El token a validar
    * @param username - El username que debería tener el token
    * @return true si es válido, false si no */
    public boolean isTokenValid(String token, String username) {
        // Extrae el username del token
        final String tokenUsername = extractUsername(token);

        // El token es válido si:
        // 1.- El usernamne coincide
        // 2.- El token no haya expirado
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }
}

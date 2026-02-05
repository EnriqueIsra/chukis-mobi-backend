package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.DashboardStatsResponse;
import com.enrique.springboot.backend.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* Controlador REST para el dashboard
* Expone endpoint para obtener estadísticas del sistema
*/
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200"})
public class DashboardController {

    // Inyectamos el servicio de dashboard
    private final DashboardService dashboardService;

    // Constructor con inyección de dependencias
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /*
    * GET /api/dashboard/stats
    * Retorna las estadisticas del dashboard
    * - rentasPorEntregar
    * - rentasPorRecoger
    * - ingresosDelMes
    */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        DashboardStatsResponse stats = dashboardService.getStats();
        return ResponseEntity.ok(stats);
    }
}

/*
* Explicación:
* @RestController - indica que es un controlador REST (respuestas en JSON)
* @RequestMapping("/api/dashboard") - Ruta base del controlador
* @CrossOrigin - Permite peticiones desde el frontend (React en el puerto 5173)
* @GetMapping("/stats") - Endpoint GET en /api/dashboard/stats
* ResponseEntity.ok() - Respuesta HTTP 200 con el body
* Siguiente paso: Frontend -> Crear dashboardService.js
*/

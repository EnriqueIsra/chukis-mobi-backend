package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.DashboardStatsResponse;
import com.enrique.springboot.backend.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Imports para los DTOs de los nuevos endpoints
import com.enrique.springboot.backend.dto.RentalDetailDTO;
import com.enrique.springboot.backend.dto.DailyIncomeDTO;

import java.util.List;

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

    // GET /api/dashboard/deliveries?days=0
    // Retorna la lista de rentas pendientes de entregar en un día específico
    // @RequestParam: captura el parámetro "days" de una URL
    // - defaultValue = "0": si no se envía, asume hoy (0)
    // - Ejemplos de uso:
    //  /api/dashboard/deliveries           -> entregas de HOY.
    //  /api/dashboard/deliveries?days=0    -> entregas de HOY
    //  /api/dashboard/deliveries?days=1
    //  -> entregas de MAÑANA
    //  ResponseEntity<List<RentalDetailDTO>>:
    //  - Retorna HTTp 200 con un JSON array de rentas
    //  - Cada renta incluye: cliente, productos, total, pagado, pendiente.
    @GetMapping("/" +
            "deliveries")
    public ResponseEntity<List<RentalDetailDTO>> getDeliveries(
            @RequestParam(defaultValue = "0") int days) {
        List<RentalDetailDTO> deliveries = dashboardService.getDeliveries(days);
        return ResponseEntity.ok(deliveries);
    }

    // GET /api/dashboard/income/monthly
    // Retorna los ingresos diarios del mes actual para la gráfica de barras apiladas
    // No necesita parámetros porque siempre retorna los datos del mes en curso
    // ResponseEntity<List<DailyIncomeDTO>>:
    // - Retorna HTTP 200 con un JSON array
    // - Cada elemento tiene: date, cobrado, anticipos, porCobrar
    // - Solo incluye días que tengan al menos una renta.
    @GetMapping("/income/monthly")
    public ResponseEntity<List<DailyIncomeDTO>> getMonthlyIncome() {
        List<DailyIncomeDTO> income = dashboardService.getMonthlyIncome();
        return ResponseEntity.ok(income);
    }

    // GET /api/dashboard/pending-rentals
    // Retorna la lista completa de rentas pendientes de entregar (status CREATED)
    // Se usa en el dropdown expandible de la StatCard "Rentas por entregar"
    // No recibe parámetros porque siempre busca todas las rentas con status CREATED
    // Retorna List<RentalDetailDTO> con: id, clientName, clientPhone, address, startDate, endDate, status, productSummary, total, totalPaid, pending.
    @GetMapping("/pending-rentals")
    public ResponseEntity<List<RentalDetailDTO>> getPendingRentals() {
        return ResponseEntity.ok(dashboardService.getPendingRentals());
    }

    // Endpoint para obtener las rentas por recoger (status DELIVERED)
    @GetMapping("/delivered-rentals")
    public ResponseEntity<List<RentalDetailDTO>> getDeliveredRentals() {
        return ResponseEntity.ok(dashboardService.getDeliveredRentals());
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

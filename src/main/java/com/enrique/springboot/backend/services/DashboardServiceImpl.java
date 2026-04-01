package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.DailyIncomeDTO;
import com.enrique.springboot.backend.dto.DashboardStatsResponse;
import com.enrique.springboot.backend.dto.RentalDetailDTO;
import com.enrique.springboot.backend.entities.Payment;
import com.enrique.springboot.backend.entities.Rental;
import com.enrique.springboot.backend.enums.RentalStatus;
import com.enrique.springboot.backend.repositories.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de dashboard
 * Calcula las estadísticas usando el RentalRepository
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    // Inyectamos el repositorio de rentas
    private final RentalRepository rentalRepository;

    // Constructor con inyección de dependencias

    public DashboardServiceImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)     // Solo lectura, no modifica datos
    public DashboardStatsResponse getStats() {

        // 1.- Contar rentas por entregar (status = CREATED)
        Long rentasPorEntregar = rentalRepository.countByStatus(RentalStatus.CREATED);

        // 2.- Contar rentas por recoger (status = DELIVERED)
        Long rentasPorRecoger = rentalRepository.countByStatus(RentalStatus.DELIVERED);

        // 3.- Calcular ingresos del mes actual
        // Obtenemos el primer dia del mes actual a las 00:00:00
        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();

        // Obtenemos el primer día del siguiente mes (para el rango exclusivo)
        LocalDateTime finMes = mesActual.plusMonths(1).atDay(1).atStartOfDay();

        // Solo contamos rentas activas (Excluimos CANCELLED)
        List<RentalStatus> statusActivos = List.of(
                RentalStatus.CREATED,
                RentalStatus.DELIVERED,
                RentalStatus.PICKED_UP
        );

        Long ingresosDelMes = rentalRepository.sumTotalByDateRangeAndStatuses(
                inicioMes,
                finMes,
                statusActivos
        );

        // 4.- Construir y retornar la respuesta
        return new DashboardStatsResponse(rentasPorEntregar, rentasPorRecoger, ingresosDelMes);
    }

    // Obtener entregas de hoy o mañana
    // daysFromToday = 0 -> entregas de HOY
    // daysFromToday = 1 -> entregas de MAÑANA
    // Busca rentas con status CREATED (pendientes de entregar)
    // cuyo startDate caiga en el día solicitado
    // Convierte cada Rental a RentalDetailDTO con:
    // - Datos del cliente
    // - Resumen de productos ("3 sillas, 2 Mesas")
    // - Total, pagado y pendiente.
    @Override
    @Transactional(readOnly = true)
    public List<RentalDetailDTO> getDeliveries(int daysFromToday) {

        // Calculamos el día objetivo sumando daysFromToday al día actual
        // Ejemplo: si hoy es 12 Feb y daysFromToday = 1, targetDate = 13 Feb
        LocalDate targetDate = LocalDate.now().plusDays(daysFromToday);

        // Inicio del día a las 00:00:00
        LocalDateTime startOfDay = targetDate.atStartOfDay();

        // Inicio del día SIGUIENTE (para el rango exclusivo < endDate)
        // Así capturamos todo el día: >= 00:00:00 y < 00:00:00 del dia siguiente
        LocalDateTime endOfDay = targetDate.plusDays(1).atStartOfDay();

        // Solo buscamos rentas CREATED (pendientes de entregar)
        List<RentalStatus> statuses = List.of(RentalStatus.CREATED);

        // Ejecutamos la query del repository
        List<Rental> rentals = rentalRepository.findByStartDateRangeAndStatuses(
                startOfDay, endOfDay, statuses
        );

        // Convertimos cada Rental a RentalDetailDTO
        // .stream() crea un flujo de datos para transformar la lista
        // .map() transforma cada elemento usando el método convertToDTO
        // .collect() junta los resultados en una nueva lista
        return rentals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener ingresos diarios del mes actual
    // Para cada día del mes que tenga rentas, calcula:
    // - cobrado: total de rentas 100% pagadas
    // - anticipos: pagos parciales recibidos
    // - porCobrar: lo que falta por cobrar
    // Usa un Map agrupado por fecha para acumular valores.
    @Override
    @Transactional(readOnly = true)
    public List<DailyIncomeDTO> getMonthlyIncome() {
        YearMonth mesActual = YearMonth.now();
        return getMonthlyIncome(mesActual.getYear(), mesActual.getMonthValue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyIncomeDTO> getMonthlyIncome(int year, int month) {

        // Rango del mes solicitado
        YearMonth mes = YearMonth.of(year, month);
        LocalDateTime inicioMes = mes.atDay(1).atStartOfDay();
        LocalDateTime finMes = mes.plusMonths(1).atDay(1).atStartOfDay();

        // Status activos (excluimos CANCELLED)
        List<RentalStatus> statusActivos = List.of(
                RentalStatus.CREATED,
                RentalStatus.DELIVERED,
                RentalStatus.PICKED_UP
        );

        // Obtenemos las rentas del mes CON sus pagos ya cargados
        // (gracias al LEFT JOIN FETCH del query del repository)
        List<Rental> rentals = rentalRepository.findRentalsWithPaymentsByDateRange(
                inicioMes, finMes, statusActivos
        );

        // LinkedHashMap mantiene el orden de insersión (por fecha)
        // Key = fecha del día, Value = DTO con los acumulados de ese día
        Map<LocalDate, DailyIncomeDTO> dailyMap = new LinkedHashMap<>();

        // Recorremos cada renta para clasificarla
        for (Rental rental : rentals) {

            // Extraemos solo la fecha (sin hora) del startDate
            // Ejemplo 2026-02-12T10:30:00 -> 2026-02-12
            LocalDate date = rental.getStartDate().toLocalDate();

            // Si este día no existe en el mapa, lo creamos con valores en 0
            // computeIfAbsent: "si la key no existe, créala con este valor"
            DailyIncomeDTO daily = dailyMap.computeIfAbsent(date,
                    d -> new DailyIncomeDTO(d, 0L, 0L, 0L));

            // Calculamos cuánto se ha pagado de esta renta sumando todos sus pagos (Payment.amount)
            Long totalPaid = calculateTotalPaid(rental);

            // El total de la renta (precio acordado)
            Long total = rental.getTotal();

            // Lo que falta por cobrar
            long pending = total - totalPaid;

            // Clasificamos según si está pagada completamente o no
            if (pending <= 0 ) {
                // PAGADA AL 100%: todo el total va a "cobrado"
                // Ejemplo: renta de $500, pagó $500 -> cobrado += $500
                daily.setCobrado(daily.getCobrado() + total);
            } else {
                // NO PAGADA COMPLETAMENTE:
                // - Lo que ya pagó va a "anticipos"
                // - Lo que falta va a "porCobrar"
                // Ejemplo: renta de $500, pagó $200 -> anticipos += $200, porCobrar += $300
                daily.setAnticipos(daily.getAnticipos() + totalPaid);
                daily.setPorCobrar(daily.getPorCobrar() + pending);
            }
        }

        // Convertimos el mapa a lista (solo los valores, sin las keys)
        return new ArrayList<>(dailyMap.values());
    }

    // Obtener todas las rentas pendientes de entregar (status CREATED)
    // No filtramos por fecha: traemos TODAS las CREATED sin importar cuándo son
    // Reutilizamos convertToDTO() que ya tenemos para transformar cada Rental a DTO.
    @Override
    @Transactional(readOnly = true)
    public List<RentalDetailDTO> getPendingRentals() {

        // findByStatus: método que Spring Data genera automáticamente
        // Busca todas las rentas cuyo status sea CREATED
        List<Rental> rentals = rentalRepository.findByStatus((RentalStatus.CREATED));

        // Convertimos cada Rental a RentalDetalDTO
        // Reutilizamos el método privado convertToDTO() que ya armamos en getDeliveries()
        // Este método extrae: cliente, teléfono, productos, total, pagado, pendiente.
        return rentals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtiene las rentas que ya fueron entregadas y que ahora están pendientes de recoger (status DELIVERED)
    // Mismo patrón que getPendingRentals pero filtrando por DELIVERED
    @Override
    @Transactional(readOnly = true)
    public List<RentalDetailDTO> getDeliveredRentals() {
        // findByStatus: método que Spring Data genera automáticamente
        // Busca todas las rentas cuyo status sea DELIVERED
        List<Rental> rentals = rentalRepository.findByStatus((RentalStatus.DELIVERED));

        // Convertimos cada Rental a RentalDetalDTO
        // Reutilizamos el método privado convertToDTO() que ya armamos en getDeliveries()
        // Este método extrae: cliente, teléfono, productos, total, pagado, pendiente.
        return rentals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método privado: Convierte una entidad Rental a RentalDetailDTO
    // Extrae los datos del cliente, arma el resumen de productos y calcula los pagos (total pagado y pendiente)
    private RentalDetailDTO convertToDTO(Rental rental) {

        // Armamos el resumen de productos
        // Ejemplo: "3 sillas, 2 Mesas, 1 Mantel"
        String productSummary = buildProductSummary(rental);

        // Calculamos el total pagado sumando todos los Payment
        Long totalPaid = calculateTotalPaid(rental);

        // Lo que falta por cobrar
        Long pending = rental.getTotal() - totalPaid;

        // Construimos el DTO con todos los datos
        return new RentalDetailDTO(
                rental.getId(),
                rental.getClient() != null ? rental.getClient().getNombre() : "Sin cliente",
                rental.getClient() != null ? rental.getClient().getTelefono() : "Sin teléfono",
                rental.getAddress(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getStatus().name(),   // .name() convierte el enum a String: CREATED -> "CREATED"
                productSummary,
                rental.getTotal(),
                totalPaid,
                pending
        );
    }

    // Método privado: Arma el resumen de productos
    // Recorre los items de la renta y concatena:
    // "cantidad NombreProducto"
    // separados por coma.
    // Ejemplo: items = [{qty: 3, product:"Silla"}, {qty:2, product:"Mesa"}]
    // Resultado: "3 silla, 2 Mesa".
    private String buildProductSummary(Rental rental) {

        // Si la renta no tiene items, retornamos texto por defecto
        if (rental.getItems() == null || rental.getItems().isEmpty()) {
            return "Sin productos";
        }

        // .stream() crea un flujo para transformar cada item
        // .map() transforma cada RentalItem a "cantidad nombre"
        // Collectors.joining(", ") une todo con comas
        return rental.getItems().stream()
                .map(item -> item.getQuantity() + " " + item.getProduct().getName())
                .collect(Collectors.joining(", "));
    }

    // Método privado: Calcula el total pagado de una renta
    // Suma todos los Payment.amount de la renta
    // Si no tiene pagos, retorna 0
    // Ejemplo: pagos = [$200, $150, $100] -> totalPaid = $450.
    private Long calculateTotalPaid(Rental rental){

        // Si no tiene pagos registrados, retornamos 0
        if (rental.getPayments() == null || rental.getPayments().isEmpty()) {
            return 0L;
        }

        // .stream() crea un flujo de los pagos
        // .mapToLong() extrae el amount de cada pago como long
        // .sum() suma todos los valores
        return  rental.getPayments().stream()
                .mapToLong(Payment::getAmount)
                .sum();
    }
}

/*
* Explicación:
* @Service - Marca la clase como un servicio de Spring
* @Transactional(readOnly = true) - Optimiza la consulta ya que solo leemos datos
* YearMonth.now() - Obtiene el año y el mes actual
* atDay(1).atStartOfDay() - Primer día del mes a medianoche
* List.of(...) - Lista inmutable de status activos (excluye CANCELLED
*
* Resumen de métodos
* getDeliveries(0) -> Entregas de hoy -> Lista de RentalDetailDTO
* getDeliveries(1) -> Entregas de mañana -> Lista de RentalDetailDTO
* getMonthlyIncome() -> Ingresos del mes por día -> Lista de DailyIncomeDTO
* convertToDTO() -> Convierte Rental -> DTO -> RentalDetailDTO
* buildProductSummary() -> "3 sillas, 2 mesas" -> String
* calculateTotalPaid() -> Suma pagos de una renta -> Long
*/

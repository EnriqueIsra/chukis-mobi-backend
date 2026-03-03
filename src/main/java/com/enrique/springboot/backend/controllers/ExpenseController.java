package com.enrique.springboot.backend.controllers;

import com.enrique.springboot.backend.dto.CreateExpenseRequest;
import com.enrique.springboot.backend.dto.ExpenseResponse;
import com.enrique.springboot.backend.entities.Expense;
import com.enrique.springboot.backend.enums.ExpenseCategory;
import com.enrique.springboot.backend.repositories.UserRepository;
import com.enrique.springboot.backend.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/* Este es el que conecta el frontend con el backend. Cada endpoint recibe una peticion HTTP, la procesa con el servicio, y devuelve una respueta JSON*/
@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200"})
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    // Lista de gastos activos
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllActive() {
        List<Expense> expenses = expenseService.findAllActive();
        List<ExpenseResponse> response = expenses.stream()
                .map(this::toExpenseResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Lista de gastos desactivados
    @GetMapping("/inactive")
    public ResponseEntity<List<ExpenseResponse>> getAllInactive() {
        List<Expense> expenses = expenseService.findAllInactive();
        List<ExpenseResponse> response = expenses.stream()
                .map(this::toExpenseResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getById(@PathVariable Long id) {
        Optional<Expense> optionalExpense = expenseService.findById(id);
        if (optionalExpense.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toExpenseResponse(optionalExpense.get()));
    }

    // Crear gasto
    // @Vaild activa las validaciones del DTO (@NotNull, @Min)
    // Si fallan, Spring retorna 400 automáticamente sin entrar al método
    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request) {
        // Buscar al usuario que registra el gasto
        // El userId viene como header o parámetro - por ahora lo recibimos como param
        // TODO: En el futuro esto se puede extraer del JWT token directamente
        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setImageUrl(request.getImageUrl());

        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        } else {
            expense.setExpenseDate(LocalDateTime.now());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                toExpenseResponse(expenseService.save(expense))
        );
    }

    // Actualizar gasto
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody CreateExpenseRequest request) {
        Optional<Expense> optionalExpense = expenseService.findById(id);
        if (optionalExpense.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Expense expense = optionalExpense.get();
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setImageUrl(request.getImageUrl());

        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        }

        return ResponseEntity.ok(toExpenseResponse(expenseService.save(expense)));
    }

    // Borrado lógico
    // Recibe un JSON con "reason" (motivo) y "userId" (quién desactiva)
    // Usamos Map<String, Object> para recibir un body flexible sin crear otro DTO
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ExpenseResponse> deactivate(@PathVariable Long id,
                                                      @RequestBody Map<String, Object> body) {
        String reason = (String) body.get("reason");
        Long userId = Long.valueOf(body.get("userId").toString());

        Optional<Expense> result = expenseService.deactivate(id, reason, userId);
        return result.map(expense -> ResponseEntity.ok(toExpenseResponse(expense))).orElseGet(() -> ResponseEntity.notFound().build());

    }

    // Reactivar gasto
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ExpenseResponse> activate(@PathVariable Long id) {
        Optional<Expense> result = expenseService.activate(id);
        return result.map(expense -> ResponseEntity.ok(toExpenseResponse(expense))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Filtrar por categoría
    // El valor viene como String en la URL y Spring lo convierte al enum automáticamente
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseResponse>> getByCategory(@PathVariable ExpenseCategory category) {
        List<Expense> expenses = expenseService.findByCategory(category);
        List<ExpenseResponse> response = expenses.stream()
                .map(this::toExpenseResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Para reportes: suma total de gastos en un periodo
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getSummary(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Long total = expenseService.sumActiveByDateRange(startDate, endDate);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // Método auxiliar: convierte Expense entity -> ExpenseResponse DTO
    private ExpenseResponse toExpenseResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getCategory().name(),       // Enum -> String ("GASOLINA")
                expense.getDescription(),
                expense.getImageUrl(),
                expense.getActive(),
                expense.getUser() != null ? expense.getUser().getUsername() : null,
                expense.getDesactivationReason(),
                expense.getDesactivatedBy() != null ? expense.getDesactivatedBy().getUsername() : null,
                expense.getDesactivationDate()
        );
    }
}

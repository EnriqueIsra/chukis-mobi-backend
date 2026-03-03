package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Expense;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.enums.ExpenseCategory;
import com.enrique.springboot.backend.repositories.ExpenseRepository;
import com.enrique.springboot.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/* Aquí va la lógica de negocio real. Los métodos deactivate y activate son los más interesantes porque manejan el borrado lógico */
@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    // Spring inyecta automáticamente los repositorios por constructor

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> findAllActive() {
        return expenseRepository.findByActiveTrueOrderByExpenseDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> findAllInactive() {
        return expenseRepository.findByActiveFalseOrderByExpenseDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }

    @Override
    @Transactional
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    @Transactional
    public Optional<Expense> deactivate(Long id, String reason, Long desactivatedByUserId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);

        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            // Marcamos como inactivo
            expense.setActive(false);
            expense.setDesactivationReason(reason);
            expense.setDesactivationDate(LocalDateTime.now());

            // Buscamos al usuario que desactivó para registrar "quién bajó"
            Optional<User> optionalUser = userRepository.findById(desactivatedByUserId);
            optionalUser.ifPresent(expense::setDesactivatedBy);

            expenseRepository.save(expense);
        }

        return optionalExpense;
    }

    @Override
    @Transactional
    public Optional<Expense> activate(Long id) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);

        if (optionalExpense.isPresent()){
            Expense expense = optionalExpense.get();
            // Reactivamos y limpiamos los campos de baja
            expense.setActive(true);
            expense.setDesactivationReason(null);
            expense.setDesactivatedBy(null);
            expense.setDesactivationDate(null);

            expenseRepository.save(expense);
        }

        return optionalExpense;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> findByCategory(ExpenseCategory category) {
        return expenseRepository.findByActiveTrueAndCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Long sumActiveByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return expenseRepository.sumActiveExpensesByDateRange(startDate, endDate);
    }
}


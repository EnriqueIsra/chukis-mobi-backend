package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllActive() {
        return repository.findByActiveTrueOrderByUsernameAsc();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllInactive() {
        return repository.findByActiveFalseOrderByUsernameAsc();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Transactional
    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Transactional
    @Override
    public User deactivate(Long id, String reason, Long desactivatedByUserId) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        user.setActive(false);
        user.setDesactivationReason(reason);
        user.setDesactivationDate(LocalDateTime.now());

        Optional<User> optionalAdmin = repository.findById(desactivatedByUserId);
        optionalAdmin.ifPresent(user::setDesactivatedBy);

        return repository.save(user);
    }

    @Transactional
    @Override
    public User activate(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        user.setActive(true);
        user.setDesactivationReason(null);
        user.setDesactivationDate(null);
        user.setDesactivatedBy(null);

        return repository.save(user);
    }

    @Override
    public Optional<User> login(String username, String password) {
        Optional<User> userOptional = repository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setPassword(null);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}

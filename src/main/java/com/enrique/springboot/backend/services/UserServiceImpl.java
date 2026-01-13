package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    // CRUD
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
        // encriptamos contraseña solo al guardar
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPassword(user.getPassword());
        return repository.save(user);
    }

    @Transactional
    @Override
    public Optional<User> deleteById(Long id) {
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isPresent()) {
            repository.deleteById(id);
            return optionalUser;
        }
        return Optional.empty();
    }

    // -------------------
    // LOGIN
    // -------------------

    @Override
    public Optional<User> login(String username, String password) {
        Optional<User> userOptional = repository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // LOGIN SIMPLE (sin encriptar todavía)
            if (user.getPassword().equals(password)) {
                user.setPassword(null); // NUNCA regresar el password
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}

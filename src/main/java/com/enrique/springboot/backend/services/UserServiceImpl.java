package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Para inyectar el bean de BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    // Inyectamos el PasswordEncoder del bean que se encuentra en SecurityConfig
    private final PasswordEncoder passwordEncoder;

    // Constructor con inyección de dependencia
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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
        // encriptamos contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

            // Verificar contraseña usando BCrypt matches()
            // Compara la contraseña en texto plano con el hash guardado
            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setPassword(null); // NUNCA regresar el password
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}

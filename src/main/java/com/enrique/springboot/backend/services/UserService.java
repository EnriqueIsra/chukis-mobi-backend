package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);

    User save(User user);
    Optional<User> deleteById(Long id);
    User login(String username, String password);
}

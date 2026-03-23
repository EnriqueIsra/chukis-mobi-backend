package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> findAllActive();
    List<Client> findAllInactive();
    List<Client> findAll();
    Optional<Client> findById(Long id);
    Client save(Client client);
    Client deactivate(Long id, String reason, Long desactivatedByUserId);
    Client activate(Long id);
}

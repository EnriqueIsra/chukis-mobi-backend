package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    List<Client> findAll();

    Optional<Client> findById(Long id);

    Client save(Client client);

    Optional<Client> deleteById(Long id);
}

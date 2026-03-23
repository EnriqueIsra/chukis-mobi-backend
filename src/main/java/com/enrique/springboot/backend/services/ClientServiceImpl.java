package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Client;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.repositories.ClientRepository;
import com.enrique.springboot.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final UserRepository userRepository;

    public ClientServiceImpl(ClientRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAllActive() {
        return repository.findByActiveTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAllInactive() {
        return repository.findByActiveFalseOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return (List<Client>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Client save(Client client) {
        return repository.save(client);
    }

    @Override
    @Transactional
    public Client deactivate(Long id, String reason, Long desactivatedByUserId) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));

        client.setActive(false);
        client.setDesactivationReason(reason);
        client.setDesactivationDate(LocalDateTime.now());

        Optional<User> optionalUser = userRepository.findById(desactivatedByUserId);
        optionalUser.ifPresent(client::setDesactivatedBy);

        return repository.save(client);
    }

    @Override
    @Transactional
    public Client activate(Long id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));

        client.setActive(true);
        client.setDesactivationReason(null);
        client.setDesactivationDate(null);
        client.setDesactivatedBy(null);

        return repository.save(client);
    }
}

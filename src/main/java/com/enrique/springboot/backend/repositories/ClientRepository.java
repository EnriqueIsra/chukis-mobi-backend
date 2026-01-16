package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long> {
}

package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByActiveTrueOrderByNombreAsc();

    List<Client> findByActiveFalseOrderByNombreAsc();
}

package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    List<Provider> findByActiveTrueOrderByNameAsc();
    List<Provider> findByActiveFalseOrderByNameAsc();
}

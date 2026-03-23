package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByNameAsc();

    List<Product> findByActiveFalseOrderByNameAsc();
}

package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByActiveTrueOrderByCreatedAtDesc();

    List<Quotation> findByActiveFalseOrderByCreatedAtDesc();
}

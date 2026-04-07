package com.enrique.springboot.backend.repositories;

import com.enrique.springboot.backend.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // Obtener todas las imágenes de un producto, ordenadas por su posición en la galería
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    // Contar cuántas imágenes tiene un producto (para asignar el displayOrder al agregar)
    Long countByProductId(Long productId);
}

package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Product;
import com.enrique.springboot.backend.entities.User;
import com.enrique.springboot.backend.repositories.ProductRepository;
import com.enrique.springboot.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ProductRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> findAllActive() {
        return repository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> findAllInactive() {
        return repository.findByActiveFalseOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> findAll() {
        return (List<Product>) repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    @Override
    public Product save(Product product) {
        return repository.save(product);
    }

    @Transactional
    @Override
    public Product deactivate(Long id, String reason, Long desactivatedByUserId) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));

        product.setActive(false);
        product.setDesactivationReason(reason);
        product.setDesactivationDate(LocalDateTime.now());

        Optional<User> optionalUser = userRepository.findById(desactivatedByUserId);
        optionalUser.ifPresent(product::setDesactivatedBy);

        return repository.save(product);
    }

    @Transactional
    @Override
    public Product activate(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));

        product.setActive(true);
        product.setDesactivationReason(null);
        product.setDesactivationDate(null);
        product.setDesactivatedBy(null);

        return repository.save(product);
    }
}

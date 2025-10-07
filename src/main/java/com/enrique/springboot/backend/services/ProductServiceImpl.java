package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.entities.Product;
import com.enrique.springboot.backend.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
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
    public Optional<Product> deleteById(Long id) {
        Optional<Product> optionalProduct = repository.findById(id);
        if (optionalProduct.isPresent()){
            repository.deleteById(id);
            return optionalProduct;
        }
        return Optional.empty();
    }
}

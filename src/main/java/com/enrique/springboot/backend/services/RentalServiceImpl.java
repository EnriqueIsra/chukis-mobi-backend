package com.enrique.springboot.backend.services;

import com.enrique.springboot.backend.dto.CreateRentalRequest;
import com.enrique.springboot.backend.dto.RentalItemRequest;
import com.enrique.springboot.backend.entities.Product;
import com.enrique.springboot.backend.entities.Rental;
import com.enrique.springboot.backend.entities.RentalItem;
import com.enrique.springboot.backend.enums.RentalStatus;
import com.enrique.springboot.backend.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final RentalItemRepository rentalItemRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public RentalServiceImpl(
            RentalRepository rentalRepository,
            RentalItemRepository rentalItemRepository,
            ProductRepository productRepository,
            ClientRepository clientRepository,
            UserRepository userRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.rentalItemRepository = rentalItemRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    // ------------------------
    // CRUD BÁSICO
    // ------------------------
    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        return (List<Rental>) rentalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findById(Long id) {
        return rentalRepository.findById(id);
    }

    // ------------------------
    // CREAR RENTA (core)
    // ------------------------

    @Override
    @Transactional
    public Rental createRental(Rental rental, LocalDate startDate, LocalDate endDate) {
        // No crear rentas sin productos
        if (rental.getItems() == null || rental.getItems().isEmpty()) {
            throw new IllegalArgumentException("La renta debe tener al menos un producto");
        }

        // 1.- Validar fechas
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser mayor a la fecha fin");
        }
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        rental.setStatus(RentalStatus.CREATED);

        long total = 0;

        // 2.- Validar disponibilidad producto por producto
        for (RentalItem item : rental.getItems()) {

            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Long rentedQuantity =
                    rentalItemRepository.getRentedQuantityByProductAndDates(
                            product.getId(),
                            startDate,
                            endDate,
                            List.of(RentalStatus.CREATED, RentalStatus.DELIVERED)
                    );

            long availableStock = product.getStock() - rentedQuantity;

            if (item.getQuantity() > availableStock) {
                throw new RuntimeException(
                        "Stock insuficiente para el producto: " + product.getName()
                );
            }

            // 3.- Congelar datos del item
            item.setRental(rental);
            item.setPrice(product.getPrice());

            total += item.getQuantity() * item.getPrice();
        }

        rental.setTotal(total);

        // 4.- Guardar renta (cascade guarda items)
        return rentalRepository.save(rental);
    }

    // ------------------------
    // CREAR RENTA FROM DTO (core)
    // ------------------------
    @Override
    @Transactional
    public Rental createRentalFromDto(CreateRentalRequest request) {

        Rental rental = new Rental();
        rental.setAddress(request.getAddress());
        rental.setClient(
                clientRepository.findById(request.getClientId())
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"))
        );
        rental.setUser(
                userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
        );

        List<RentalItem> items = request.getItems().stream().map(itemReq -> {
            RentalItem item = new RentalItem();
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            return item;
        }).toList();

        rental.setItems(items);

        return createRental(rental, request.getStartDate(), request.getEndDate());
    }


    // ------------------------
    // UPDATE RENTA FROM DTO
    // ------------------------
    @Override
    @Transactional
    public Rental updateRentalFromDto(Long id, CreateRentalRequest request) {
        Rental existingRental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Renta no encontrada"));

        // Eliminar items anteriores
        existingRental.getItems().clear();

        // Actualizar datos básicos
        existingRental.setAddress(request.getAddress());
        existingRental.setStartDate(request.getStartDate());
        existingRental.setEndDate(request.getEndDate());
        existingRental.setClient(
                clientRepository.findById(request.getClientId())
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"))
        );
        existingRental.setUser(
                userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
        );

        // Crear nuevos items y calcular total
        long total = 0;
        for (RentalItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar disponibilidad (excluyendo la renta actual)
            Long rentedQuantity = rentalItemRepository.getRentedQuantityByProductAndDatesExcludingRental(
                    product.getId(),
                    request.getStartDate(),
                    request.getEndDate(),
                    List.of(RentalStatus.CREATED, RentalStatus.DELIVERED),
                    id
            );

            long availableStock = product.getStock() - rentedQuantity;
            if (itemReq.getQuantity() > availableStock) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            RentalItem item = new RentalItem();
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());
            item.setRental(existingRental);
            existingRental.getItems().add(item);

            total += item.getQuantity() * item.getPrice();
        }

        existingRental.setTotal(total);

        return rentalRepository.save(existingRental);
    }

    // ------------------------
    // DELETE
    // ------------------------
    @Override
    public Optional<Rental> deleteById(Long id) {
        Optional<Rental> optionalRental = rentalRepository.findById(id);
        optionalRental.ifPresent(rentalRepository::delete);
        return optionalRental;
    }
    @Override
    public Rental updateStatus(Long id, String status) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        try {
            RentalStatus rentalStatus = RentalStatus.valueOf(status);
            rental.setStatus(rentalStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid rental status: " + status);
        }
        return rentalRepository.save(rental);
    }
}

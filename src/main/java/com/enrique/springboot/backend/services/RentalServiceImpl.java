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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final RentalItemRepository rentalItemRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public RentalServiceImpl(
            RentalRepository rentalRepository,
            RentalItemRepository rentalItemRepository,
            ProductRepository productRepository,
            ClientRepository clientRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.rentalItemRepository = rentalItemRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAllActive() {
        return rentalRepository.findByActiveTrueOrderByStartDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAllInactive() {
        return rentalRepository.findByActiveFalseOrderByStartDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        return (List<Rental>) rentalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findWithSubcontract() {
        return rentalRepository.findByActiveTrueAndHasSubcontractTrueOrderByStartDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findWithContract() {
        return rentalRepository.findByActiveTrueAndHasContractTrueOrderByContractDateDesc();
    }

    @Override
    @Transactional
    public Rental save(Rental rental) {
        return rentalRepository.save(rental);
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
    public Rental createRental(Rental rental, LocalDateTime startDate, LocalDateTime endDate) {
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

            // quantity = propias solamente, validar contra stock
            if (item.getQuantity() > availableStock) {
                throw new RuntimeException(
                        "Stock insuficiente para el producto: " + product.getName()
                );
            }

            // 3.- Congelar datos del item
            item.setRental(rental);
            item.setPrice(product.getPrice());

            // Total cliente = (propias + subcontratadas) * precio
            int subcontractedQty = item.getSubcontractedQuantity() != null ? item.getSubcontractedQuantity() : 0;
            total += (item.getQuantity() + subcontractedQty) * item.getPrice();
        }

        // Respeta el total calculado personalizado si viene, si no usa el calculado
        if (rental.getTotal() != null && rental.getTotal() > 0) {
            // El total ya fue seteado desde el DTO con valor personalizado, no lo pisamos
        } else {
            rental.setTotal(total);
        }

        // Si al menos un item tiene subcontractedQuantity > 0, marcar la renta
        boolean hasSubcontract = rental.getItems().stream()
                .anyMatch(i -> i.getSubcontractedQuantity() != null && i.getSubcontractedQuantity() > 0);
        rental.setHasSubcontract(hasSubcontract);

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
            item.setSubcontractedQuantity(itemReq.getSubcontractedQuantity() != null ? itemReq.getSubcontractedQuantity() : 0);
            return item;
        }).toList();

        rental.setItems(items);

        if (request.getTotal() != null && request.getTotal() > 0) {
            rental.setTotal((request.getTotal())); // Pre-setea el total personalizado
        }

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

            // quantity = propias solamente
            if (itemReq.getQuantity() > availableStock) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            int subcontractedQty = itemReq.getSubcontractedQuantity() != null ? itemReq.getSubcontractedQuantity() : 0;

            RentalItem item = new RentalItem();
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setSubcontractedQuantity(subcontractedQty);
            item.setPrice(product.getPrice());
            item.setRental(existingRental);
            existingRental.getItems().add(item);

            // Total cliente = (propias + subcontratadas) * precio
            total += (item.getQuantity() + subcontractedQty) * item.getPrice();
        }

        if (request.getTotal() != null && request.getTotal() > 0) {
            existingRental.setTotal(request.getTotal());
        } else {
            existingRental.setTotal(total);
        }

        // Actualizar bandera de subcontrato
        boolean hasSubcontract = existingRental.getItems().stream()
                .anyMatch(i -> i.getSubcontractedQuantity() != null && i.getSubcontractedQuantity() > 0);
        existingRental.setHasSubcontract(hasSubcontract);

        return rentalRepository.save(existingRental);
    }

    @Override
    @Transactional
    public Rental deactivate(Long id, String reason, Long desactivatedByUserId) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Renta no encontrada: " + id));

        rental.setActive(false);
        rental.setDesactivationReason(reason);
        rental.setDesactivationDate(LocalDateTime.now());

        Optional<com.enrique.springboot.backend.entities.User> optionalUser = userRepository.findById(desactivatedByUserId);
        optionalUser.ifPresent(rental::setDesactivatedBy);

        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental activate(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Renta no encontrada: " + id));

        rental.setActive(true);
        rental.setDesactivationReason(null);
        rental.setDesactivationDate(null);
        rental.setDesactivatedBy(null);

        return rentalRepository.save(rental);
    }

    @Override
    public Rental updateStatus(Long id, String status) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        try {
            RentalStatus rentalStatus = RentalStatus.valueOf(status);

            // Validar que no se pueda marcar como RECOGIDA si falta pago
            if (rentalStatus == RentalStatus.PICKED_UP) {
                Long totalPaid = paymentRepository.sumAmountByRentalId(rental.getId());
                long pending = rental.getTotal() - totalPaid;
                if (pending > 0) {
                    throw new RuntimeException("No se puede marcar como recogida. Falta por cobrar: $" + pending);
                }
            }

            rental.setStatus(rentalStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid rental status: " + status);
        }
        return rentalRepository.save(rental);
    }
}

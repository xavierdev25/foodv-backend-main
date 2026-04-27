package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.FindOrderUseCase;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;
import com.foodv.backend.domain.port.out.UserRepositoryPort;


import java.util.List;

@Service
@RequiredArgsConstructor
public class FindOrderHandler implements FindOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Order findById(Long id) {
        return orderRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));
    }

    @Override
    public Order findByIdForUser(Long orderId, String email) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isOwner = order.getUserId().equals(user.getId());
        boolean isStoreOwner = user.getRole() == UserRole.COMERCIO;
        boolean isDelivery = user.getRole() == UserRole.REPARTIDOR;

        if (!isAdmin && !isOwner && !isStoreOwner && !isDelivery) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para ver esta orden"
            );
        }
        return order;
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepositoryPort.findByUserId(userId);
    }

    @Override
    public List<Order> findByStoreId(Long storeId) {
        return orderRepositoryPort.findByStoreId(storeId);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepositoryPort.findByStatus(status);
    }

    @Override
    public List<Order> findAll() {
        return orderRepositoryPort.findAll();
    }

    @Override
    public Page<Order> findAllPaginated(Pageable pageable) {
        return orderRepositoryPort.findAllPaginated(pageable);
    }

    @Override
    public Page<Order> findByUserIdPaginated(Long userId, Pageable pageable) {
        return orderRepositoryPort.findByUserIdPaginated(userId, pageable);
    }

    @Override
    public Page<Order> findByStoreIdPaginated(Long storeId, Pageable pageable) {
        return orderRepositoryPort.findByStoreIdPaginated(storeId, pageable);
    }
}

package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.FindOrderUseCase;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindOrderHandler implements FindOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    public Order findById(Long id) {
        return orderRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));
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
}

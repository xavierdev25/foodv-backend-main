package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;
    private final OrderEntityMapper mapper;

    @Override
    public Order save(Order order) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Order> findByStoreId(Long storeId) {
        return jpaRepository.findByStoreId(storeId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return jpaRepository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Page<Order> findAllPaginated(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByUserIdPaginated(Long userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByStoreIdPaginated(Long storeId, Pageable pageable) {
        return jpaRepository.findByStoreId(storeId, pageable).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByUserIdAndStatus(Long userId, OrderStatus status) {
        return jpaRepository.findByUserIdAndStatus(userId, status).stream().map(mapper::toDomain).toList();
    }
}

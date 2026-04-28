package com.foodv.backend.infrastructure.persistence.adapter;

import com.foodv.backend.domain.common.PageQuery;
import com.foodv.backend.domain.common.PagedResult;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.infrastructure.common.PagingMapper;
import com.foodv.backend.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
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
    public Optional<Order> findByIdWithItems(Long id) {
        return jpaRepository.findByIdWithItems(id).map(mapper::toDomain);
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
    public PagedResult<Order> findAllPaginated(PageQuery query) {
        return PagingMapper.toDomain(
                jpaRepository.findAll(PagingMapper.toPageable(query)),
                mapper::toDomain
        );
    }

    @Override
    public PagedResult<Order> findByUserIdPaginated(Long userId, PageQuery query) {
        return PagingMapper.toDomain(
                jpaRepository.findByUserId(userId, PagingMapper.toPageable(query)),
                mapper::toDomain
        );
    }

    @Override
    public PagedResult<Order> findByStoreIdPaginated(Long storeId, PageQuery query) {
        return PagingMapper.toDomain(
                jpaRepository.findByStoreId(storeId, PagingMapper.toPageable(query)),
                mapper::toDomain
        );
    }

    @Override
    public PagedResult<Order> findByStatusPaginated(OrderStatus status, PageQuery query) {
        return PagingMapper.toDomain(
                jpaRepository.findByStatus(status, PagingMapper.toPageable(query)),
                mapper::toDomain
        );
    }

    @Override
    public List<Order> findByUserIdAndStatus(Long userId, OrderStatus status) {
        return jpaRepository.findByUserIdAndStatus(userId, status).stream().map(mapper::toDomain).toList();
    }
}

package com.foodv.backend.domain.port.in.order;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FindOrderUseCase {

    Order findById(Long id);

    List<Order> findByUserId(Long userId);

    List<Order> findByStoreId(Long storeId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();

    Page<Order> findAllPaginated(Pageable pageable);

    Page<Order> findByUserIdPaginated(Long userId, Pageable pageable);

    Page<Order> findByStoreIdPaginated(Long storeId, Pageable pageable);
}

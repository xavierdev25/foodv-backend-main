package com.foodv.backend.domain.port.in.order;

import com.foodv.backend.domain.common.PageQuery;
import com.foodv.backend.domain.common.PagedResult;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;

import java.util.List;

public interface FindOrderUseCase {

    Order findById(Long id);

    List<Order> findByUserId(Long userId);

    List<Order> findByStoreId(Long storeId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();

    PagedResult<Order> findAllPaginated(PageQuery query);

    PagedResult<Order> findByUserIdPaginated(Long userId, PageQuery query);

    PagedResult<Order> findByStoreIdPaginated(Long storeId, PageQuery query);

    PagedResult<Order> findByStatusPaginated(OrderStatus status, PageQuery query);

    Order findByIdForUser(Long orderId, String email);

    PagedResult<Order> findForUser(String email, PageQuery query);
}

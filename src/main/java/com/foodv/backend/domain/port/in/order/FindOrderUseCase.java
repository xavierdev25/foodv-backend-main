package com.foodv.backend.domain.port.in.order;

import com.foodv.backend.domain.common.PageQuery;
import com.foodv.backend.domain.common.PagedResult;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;

public interface FindOrderUseCase {

    Order findById(Long id);

    PagedResult<Order> findAllPaginated(PageQuery query);

    PagedResult<Order> findByUserIdPaginated(Long userId, PageQuery query);

    PagedResult<Order> findByStoreIdPaginated(Long storeId, PageQuery query);

    PagedResult<Order> findByStatusPaginated(OrderStatus status, PageQuery query);

    Order findByIdForUser(Long orderId, String email);

    PagedResult<Order> findForUser(String email, PageQuery query);
}

package com.foodv.backend.application.order;

import com.foodv.backend.domain.common.PageQuery;
import com.foodv.backend.domain.common.PagedResult;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.model.user.UserRole;
import com.foodv.backend.domain.port.in.order.FindOrderUseCase;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.port.out.StoreRepositoryPort;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindOrderHandler implements FindOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final StoreRepositoryPort storeRepositoryPort;

    @Override
    public Order findById(Long id) {
        return orderRepositoryPort.findByIdWithItems(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));
    }

    @Override
    public Order findByIdForUser(Long orderId, String email) {
        Order order = orderRepositoryPort.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("No autenticado"));

        if (!canAccess(user, order)) {
            throw new AccessDeniedException("No tienes permiso para ver esta orden");
        }
        return order;
    }

    @Override
    public PagedResult<Order> findForUser(String email, PageQuery query) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("No autenticado"));

        if (user.getRole() == UserRole.ADMIN) {
            return orderRepositoryPort.findAllPaginated(query);
        }
        if (user.getRole() == UserRole.COMERCIO) {
            Long storeId = storeRepositoryPort.findByOwnerId(user.getId())
                    .map(Store::getId)
                    .orElseThrow(() -> new AccessDeniedException("No tienes una tienda asociada"));
            return orderRepositoryPort.findByStoreIdPaginated(storeId, query);
        }
        return orderRepositoryPort.findByUserIdPaginated(user.getId(), query);
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
    public PagedResult<Order> findAllPaginated(PageQuery query) {
        return orderRepositoryPort.findAllPaginated(query);
    }

    @Override
    public PagedResult<Order> findByUserIdPaginated(Long userId, PageQuery query) {
        return orderRepositoryPort.findByUserIdPaginated(userId, query);
    }

    @Override
    public PagedResult<Order> findByStoreIdPaginated(Long storeId, PageQuery query) {
        return orderRepositoryPort.findByStoreIdPaginated(storeId, query);
    }

    @Override
    public PagedResult<Order> findByStatusPaginated(OrderStatus status, PageQuery query) {
        return orderRepositoryPort.findByStatusPaginated(status, query);
    }

    private boolean canAccess(User user, Order order) {
        if (user.getRole() == UserRole.ADMIN) return true;
        if (order.getUserId() != null && order.getUserId().equals(user.getId())) return true;
        if (user.getRole() == UserRole.COMERCIO) {
            return storeRepositoryPort.findByOwnerId(user.getId())
                    .map(s -> s.getId().equals(order.getStoreId()))
                    .orElse(false);
        }
        if (user.getRole() == UserRole.REPARTIDOR) {
            return user.getId().equals(order.getRepartidorId());
        }
        return false;
    }
}

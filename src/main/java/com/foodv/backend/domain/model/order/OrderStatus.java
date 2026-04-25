package com.foodv.backend.domain.model.order;

import java.util.Set;

public enum OrderStatus {
    PENDIENTE,
    PREPARANDO,
    EN_CAMINO,
    ENTREGADO,
    CANCELADO;

    public boolean canTransitionTo(OrderStatus nextStatus) {
        return switch (this) {
            case PENDIENTE -> Set.of(PREPARANDO, CANCELADO).contains(nextStatus);
            case PREPARANDO -> Set.of(EN_CAMINO, CANCELADO).contains(nextStatus);
            case EN_CAMINO -> nextStatus == ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }
}

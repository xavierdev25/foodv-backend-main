package com.foodv.backend.domain.service;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Servicio de dominio para la lógica de negocio de órdenes.
 * Encapsula reglas de negocio que no pertenecen a una entidad específica
 * pero son parte del dominio — sin dependencias de framework.
 */
@Component
public class OrderDomainService {

    /**
     * Valida y aplica una transición de estado a una orden.
     * Implementa la máquina de estados:
     * PENDIENTE → PREPARANDO → LISTO_PARA_RECOGER → EN_CAMINO → ENTREGADO
     * PENDIENTE → CANCELADO
     * PREPARANDO → CANCELADO
     * LISTO_PARA_RECOGER → CANCELADO
     * EN_CAMINO → CANCELADO
     *
     * @throws IllegalArgumentException si la transición no es válida
     */
    public Order applyStatusTransition(Order order, OrderStatus newStatus) {
        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                    "Transición de estado inválida: " + order.getStatus() + " -> " + newStatus
            );
        }
        return Order.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .storeId(order.getStoreId())
                .aulaId(order.getAulaId())
                .repartidorId(order.getRepartidorId())
                .items(order.getItems())
                .total(order.getTotal())
                .propina(order.getPropina())
                .tarifaServicio(order.getTarifaServicio())
                .comisionFoodv(order.getComisionFoodv())
                .status(newStatus)
                .notas(order.getNotas())
                .motivoCancelacion(order.getMotivoCancelacion())
                .canceladoPor(order.getCanceladoPor())
                .codigoConfirmacion(order.getCodigoConfirmacion())
                .fotoEntregaUrl(order.getFotoEntregaUrl())
                .creadoEn(order.getCreadoEn())
                .actualizadoEn(LocalDateTime.now())
                .build();
    }

    /**
     * Verifica si una orden puede ser cancelada.
     */
    public boolean isCancellable(Order order) {
        return order.getStatus().canTransitionTo(OrderStatus.CANCELADO);
    }

    /**
     * Verifica si una orden está en un estado terminal (no puede cambiar más).
     */
    public boolean isTerminal(Order order) {
        return order.getStatus() == OrderStatus.ENTREGADO
                || order.getStatus() == OrderStatus.CANCELADO;
    }
}

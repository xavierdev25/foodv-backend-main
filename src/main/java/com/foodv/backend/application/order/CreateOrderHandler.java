package com.foodv.backend.application.order;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderItem;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.port.in.order.CreateOrderUseCase;
import com.foodv.backend.domain.port.out.AulaRepositoryPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import com.foodv.backend.domain.port.out.StoreRepositoryPort;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import com.foodv.backend.infrastructure.metrics.BusinessMetricsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderHandler implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final StoreRepositoryPort storeRepositoryPort;
    private final AulaRepositoryPort aulaRepositoryPort;
    private final BusinessMetricsService metricsService;

    @Override
    public Order execute(CreateOrderCommand command) {
        userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Store store = storeRepositoryPort.findById(command.storeId())
                .orElseThrow(() -> new EntityNotFoundException("Tienda no encontrada"));

        if (!store.isActivo()) {
            throw new IllegalArgumentException("La tienda no está activa");
        }

        aulaRepositoryPort.findById(command.aulaId())
                .orElseThrow(() -> new EntityNotFoundException("Aula no encontrada"));

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemCommand itemCommand : command.items()) {
            Product product = productRepositoryPort.findById(itemCommand.productId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + itemCommand.productId()));

            if (!product.isDisponible()) {
                throw new IllegalArgumentException("Producto no disponible: " + product.getNombre());
            }

            if (product.getStock() < itemCommand.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + product.getNombre());
            }

            BigDecimal subtotal = product.getPrecio().multiply(BigDecimal.valueOf(itemCommand.cantidad()));

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productNombre(product.getNombre())
                    .productPrecio(product.getPrecio())
                    .cantidad(itemCommand.cantidad())
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
        }

        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(command.userId())
                .storeId(command.storeId())
                .aulaId(command.aulaId())
                .items(orderItems)
                .total(total)
                .propina(command.propina())
                .tarifaServicio(BigDecimal.valueOf(1.50))
                .comisionFoodv(BigDecimal.valueOf(0.50))
                .codigoConfirmacion(String.format("%04d", new java.util.Random().nextInt(10000)))
                .status(OrderStatus.PENDIENTE)
                .notas(command.notas())
                .creadoEn(LocalDateTime.now())
                .actualizadoEn(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepositoryPort.save(order);

        savedOrder.getItems().forEach(item ->
            productRepositoryPort.findById(item.getProductId()).ifPresent(product -> {
                Product updatedProduct = Product.builder()
                    .id(product.getId())
                    .nombre(product.getNombre())
                    .descripcion(product.getDescripcion())
                    .precio(product.getPrecio())
                    .stock(product.getStock() - item.getCantidad())
                    .imagenUrl(product.getImagenUrl())
                    .categoria(product.getCategoria())
                    .storeId(product.getStoreId())
                    .activo(product.isActivo())
                    .disponible(product.getStock() - item.getCantidad() > 0)
                    .creadoEn(product.getCreadoEn())
                    .build();
                productRepositoryPort.save(updatedProduct);
            })
        );

        metricsService.recordOrderCreated();

        return savedOrder;
    }
}

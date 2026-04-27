package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.CancelOrderUseCase;
import com.foodv.backend.domain.port.in.order.CreateOrderUseCase;
import com.foodv.backend.domain.port.in.order.FindOrderUseCase;
import com.foodv.backend.domain.port.in.order.UpdateOrderStatusUseCase;
import com.foodv.backend.infrastructure.persistence.repository.OrderStatusHistoryRepository;
import com.foodv.backend.infrastructure.web.dto.order.CreateOrderRequest;
import com.foodv.backend.infrastructure.web.dto.order.OrderResponse;
import com.foodv.backend.infrastructure.web.dto.order.OrderStatusHistoryResponse;
import com.foodv.backend.infrastructure.web.dto.order.UpdateOrderStatusRequest;
import com.foodv.backend.infrastructure.web.mapper.OrderWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import com.foodv.backend.infrastructure.config.JwtService;

import java.util.List;

@Tag(name = "Órdenes")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final FindOrderUseCase findOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderWebMapper mapper;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final JwtService jwtService;

    @Operation(summary = "Crear orden")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        Order order = createOrderUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(order));
    }

    @Operation(summary = "Listar órdenes paginado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de órdenes"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(findOrderUseCase.findAllPaginated(pageable).map(mapper::toResponse));
    }

    @Operation(summary = "Obtener orden")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        return ResponseEntity.ok(mapper.toResponse(findOrderUseCase.findByIdForUser(id, email)));
    }

    @Operation(summary = "Órdenes por usuario")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrderResponse>> findByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(findOrderUseCase.findByUserIdPaginated(userId, pageable).map(mapper::toResponse));
    }

    @Operation(summary = "Órdenes por tienda")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Page<OrderResponse>> findByStoreId(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(findOrderUseCase.findByStoreIdPaginated(storeId, pageable).map(mapper::toResponse));
    }

    @Operation(summary = "Órdenes por estado")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> findByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> responses = findOrderUseCase.findByStatus(status).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Cambiar estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "400", description = "Transición inválida"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        Order order = updateOrderStatusUseCase.execute(id, request.status());
        return ResponseEntity.ok(mapper.toResponse(order));
    }

    @Operation(summary = "Cancelar orden")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden cancelada"),
        @ApiResponse(responseCode = "400", description = "No se puede cancelar"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        Order order = cancelOrderUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponse(order));
    }

    @Operation(summary = "Historial de estados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}/history")
    public ResponseEntity<List<OrderStatusHistoryResponse>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(
            orderStatusHistoryRepository.findByOrderIdOrderByCreadoEnAsc(id)
                .stream()
                .map(h -> new OrderStatusHistoryResponse(
                    h.getId(), h.getOrderId(), h.getStatus(),
                    h.getChangedBy(), h.getNotas(), h.getCreadoEn()
                ))
                .toList()
        );
    }
}

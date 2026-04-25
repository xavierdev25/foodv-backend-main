package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.port.in.order.CancelOrderUseCase;
import com.foodv.backend.domain.port.in.order.CreateOrderUseCase;
import com.foodv.backend.domain.port.in.order.FindOrderUseCase;
import com.foodv.backend.domain.port.in.order.UpdateOrderStatusUseCase;
import com.foodv.backend.infrastructure.web.dto.order.CreateOrderRequest;
import com.foodv.backend.infrastructure.web.dto.order.OrderResponse;
import com.foodv.backend.infrastructure.web.dto.order.UpdateOrderStatusRequest;
import com.foodv.backend.infrastructure.web.mapper.OrderWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final FindOrderUseCase findOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderWebMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        Order order = createOrderUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        List<OrderResponse> responses = findOrderUseCase.findAll().stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        Order order = findOrderUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> findByUserId(@PathVariable Long userId) {
        List<OrderResponse> responses = findOrderUseCase.findByUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<OrderResponse>> findByStoreId(@PathVariable Long storeId) {
        List<OrderResponse> responses = findOrderUseCase.findByStoreId(storeId).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> findByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> responses = findOrderUseCase.findByStatus(status).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        Order order = updateOrderStatusUseCase.execute(id, request.status());
        return ResponseEntity.ok(mapper.toResponse(order));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        Order order = cancelOrderUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponse(order));
    }
}

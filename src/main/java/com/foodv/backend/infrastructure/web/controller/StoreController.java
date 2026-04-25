package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.port.in.store.CreateStoreUseCase;
import com.foodv.backend.domain.port.in.store.DeleteStoreUseCase;
import com.foodv.backend.domain.port.in.store.FindStoreUseCase;
import com.foodv.backend.domain.port.in.store.UpdateStoreUseCase;
import com.foodv.backend.infrastructure.web.dto.store.CreateStoreRequest;
import com.foodv.backend.infrastructure.web.dto.store.StoreResponse;
import com.foodv.backend.infrastructure.web.dto.store.UpdateStoreRequest;
import com.foodv.backend.infrastructure.web.mapper.StoreWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final CreateStoreUseCase createStoreUseCase;
    private final FindStoreUseCase findStoreUseCase;
    private final UpdateStoreUseCase updateStoreUseCase;
    private final DeleteStoreUseCase deleteStoreUseCase;
    private final StoreWebMapper mapper;

    @PostMapping
    public ResponseEntity<StoreResponse> create(@Valid @RequestBody CreateStoreRequest request) {
        Store store = createStoreUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(store));
    }

    @GetMapping
    public ResponseEntity<List<StoreResponse>> findAll() {
        List<StoreResponse> responses = findStoreUseCase.findAll().stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<StoreResponse>> findAllActivas() {
        List<StoreResponse> responses = findStoreUseCase.findAllActivas().stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> findById(@PathVariable Long id) {
        Store store = findStoreUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(store));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<StoreResponse> findByOwnerId(@PathVariable Long ownerId) {
        Store store = findStoreUseCase.findByOwnerId(ownerId);
        return ResponseEntity.ok(mapper.toResponse(store));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateStoreRequest request) {
        Store store = updateStoreUseCase.execute(id, mapper.toCommand(request));
        return ResponseEntity.ok(mapper.toResponse(store));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteStoreUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

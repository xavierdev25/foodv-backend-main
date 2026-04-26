package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.user.ChangePasswordUseCase;
import com.foodv.backend.domain.port.in.user.CreateUserUseCase;
import com.foodv.backend.domain.port.in.user.DeleteUserUseCase;
import com.foodv.backend.domain.port.in.user.FindUserUseCase;
import com.foodv.backend.domain.port.in.user.UpdateUserUseCase;
import com.foodv.backend.infrastructure.config.JwtService;
import com.foodv.backend.infrastructure.web.dto.user.ChangePasswordRequest;
import com.foodv.backend.infrastructure.web.dto.user.CreateUserRequest;
import com.foodv.backend.infrastructure.web.dto.user.UpdateUserRequest;
import com.foodv.backend.infrastructure.web.dto.user.UserResponse;
import com.foodv.backend.infrastructure.web.mapper.UserWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UserWebMapper mapper;
    private final JwtService jwtService;

    public UserController(CreateUserUseCase createUserUseCase,
                          FindUserUseCase findUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          ChangePasswordUseCase changePasswordUseCase,
                          UserWebMapper mapper,
                          JwtService jwtService) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.mapper = mapper;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User user = createUserUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(201).body(mapper.toResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(
                findUserUseCase.findAll()
                        .stream()
                        .map(mapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(findUserUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(mapper.toResponse(updateUserUseCase.execute(id, mapper.toCommand(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return ResponseEntity.ok(mapper.toResponse(findUserUseCase.findByEmail(email)));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid ChangePasswordRequest request) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        changePasswordUseCase.execute(email, request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
    }
}

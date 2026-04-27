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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import com.foodv.backend.infrastructure.persistence.repository.UserJpaRepository;
import com.foodv.backend.infrastructure.persistence.adapter.UserEntityMapper;

import java.util.List;
import java.util.Map;

@Tag(name = "Usuarios")
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
    private final UserEntityMapper userEntityMapper;
    private final UserJpaRepository userJpaRepository;

    public UserController(CreateUserUseCase createUserUseCase,
                          FindUserUseCase findUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          ChangePasswordUseCase changePasswordUseCase,
                          UserWebMapper mapper,
                          JwtService jwtService,
                          UserEntityMapper userEntityMapper,
                          UserJpaRepository userJpaRepository) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.mapper = mapper;
        this.jwtService = jwtService;
        this.userEntityMapper = userEntityMapper;
        this.userJpaRepository = userJpaRepository;
    }

    @Operation(summary = "Crear usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User user = createUserUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(201).body(mapper.toResponse(user));
    }

    @Operation(summary = "Listar usuarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuarios"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(
                findUserUseCase.findAll()
                        .stream()
                        .map(mapper::toResponse)
                        .toList()
        );
    }

    @Operation(summary = "Obtener usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(findUserUseCase.findById(id)));
    }

    @Operation(summary = "Actualizar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(mapper.toResponse(updateUserUseCase.execute(id, mapper.toCommand(request))));
    }

    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mi perfil")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil del usuario autenticado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return ResponseEntity.ok(mapper.toResponse(findUserUseCase.findByEmail(email)));
    }

    @Operation(summary = "Cambiar contraseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid ChangePasswordRequest request) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        changePasswordUseCase.execute(email, request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar usuarios eliminados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios eliminados")
    public ResponseEntity<List<UserResponse>> findDeleted() {
        return ResponseEntity.ok(
                userJpaRepository.findAllByDeletedAtIsNotNull().stream()
                        .map(userEntityMapper::toDomain)
                        .map(mapper::toResponse)
                        .toList()
        );
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restaurar usuario eliminado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario restaurado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        userJpaRepository.findById(id).ifPresent(user -> {
            user.setDeletedAt(null);
            userJpaRepository.save(user);
        });
        return ResponseEntity.ok(Map.of("message", "Usuario restaurado exitosamente"));
    }
}

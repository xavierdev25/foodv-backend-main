package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.ai.AiRecommendationResponse;
import com.foodv.backend.domain.port.in.ai.GetRecommendationsUseCase;
import com.foodv.backend.infrastructure.web.dto.ai.RecommendationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inteligencia Artificial")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final GetRecommendationsUseCase getRecommendationsUseCase;
    private final com.foodv.backend.domain.port.out.UserRepositoryPort userRepositoryPort;

    @Operation(summary = "Recomendaciones personalizadas con IA basadas en tu perfil e historial")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recomendaciones generadas"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "500", description = "Error en el servicio de IA")
    })
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam(defaultValue = "5") int maxRecommendations) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Long userId = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"))
                .getId();

        GetRecommendationsUseCase.GetRecommendationsCommand command =
                new GetRecommendationsUseCase.GetRecommendationsCommand(userId, maxRecommendations);

        AiRecommendationResponse result = getRecommendationsUseCase.execute(command);

        List<RecommendationResponse.ProductRecommendationDto> dtos = result.getRecommendations().stream()
                .map(r -> new RecommendationResponse.ProductRecommendationDto(
                        r.productId(),
                        r.nombre(),
                        r.precio(),
                        r.categoria(),
                        r.score(),
                        r.reason()
                ))
                .toList();

        return ResponseEntity.ok(new RecommendationResponse(
                result.getUserId(),
                dtos,
                result.getGeneratedBy()
        ));
    }
}
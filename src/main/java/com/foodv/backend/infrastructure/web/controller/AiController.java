package com.foodv.backend.infrastructure.web.controller;

import com.foodv.backend.domain.model.ai.AiRecommendationResponse;
import com.foodv.backend.domain.port.in.ai.GetRecommendationsUseCase;
import com.foodv.backend.infrastructure.web.dto.ai.RecommendationRequest;
import com.foodv.backend.infrastructure.web.dto.ai.RecommendationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inteligencia Artificial")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final GetRecommendationsUseCase getRecommendationsUseCase;

    @Operation(summary = "Recomendaciones personalizadas con IA")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recomendaciones generadas"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "500", description = "Error en el servicio de IA")
    })
    @PostMapping("/recommendations")
    public ResponseEntity<RecommendationResponse> getRecommendations(@Valid @RequestBody RecommendationRequest request) {
        GetRecommendationsUseCase.GetRecommendationsCommand command = new GetRecommendationsUseCase.GetRecommendationsCommand(
                request.userId(),
                request.restrictions(),
                request.preferences(),
                request.maxRecommendations()
        );

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

        RecommendationResponse response = new RecommendationResponse(
                result.getUserId(),
                dtos,
                result.getGeneratedBy()
        );

        return ResponseEntity.ok(response);
    }
}

package com.foodv.backend.application.ai;

import com.foodv.backend.domain.model.ai.AiRecommendationRequest;
import com.foodv.backend.domain.model.ai.AiRecommendationResponse;
import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.port.in.ai.GetRecommendationsUseCase;
import com.foodv.backend.domain.port.out.AiRecommendationPort;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRecommendationsHandler implements GetRecommendationsUseCase {

    private final AiRecommendationPort aiRecommendationPort;
    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public AiRecommendationResponse execute(GetRecommendationsCommand command) {
        List<Product> products = productRepositoryPort.findAll();

        List<AiRecommendationRequest.ProductInfo> productInfos = products.stream()
                .filter(Product::isActivo)
                .map(p -> new AiRecommendationRequest.ProductInfo(
                        p.getId(),
                        p.getNombre(),
                        p.getPrecio(),
                        p.getCategoria().name()
                ))
                .toList();

        AiRecommendationRequest request = AiRecommendationRequest.builder()
                .userId(command.userId())
                .restrictions(command.restrictions())
                .preferences(command.preferences())
                .availableProducts(productInfos)
                .maxRecommendations(command.maxRecommendations())
                .build();

        return aiRecommendationPort.getRecommendations(request);
    }
}

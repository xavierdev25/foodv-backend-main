package com.foodv.backend.application.ai;

import com.foodv.backend.domain.model.ai.AiRecommendationRequest;
import com.foodv.backend.domain.model.ai.AiRecommendationResponse;
import com.foodv.backend.domain.model.order.Order;
import com.foodv.backend.domain.model.order.OrderItem;
import com.foodv.backend.domain.model.order.OrderStatus;
import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.user.User;
import com.foodv.backend.domain.port.in.ai.GetRecommendationsUseCase;
import com.foodv.backend.domain.port.out.AiRecommendationPort;
import com.foodv.backend.domain.port.out.OrderRepositoryPort;
import com.foodv.backend.domain.port.out.ProductRepositoryPort;
import com.foodv.backend.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRecommendationsHandler implements GetRecommendationsUseCase {

    private final AiRecommendationPort aiRecommendationPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    public AiRecommendationResponse execute(GetRecommendationsCommand command) {
        // 1. Obtener perfil del usuario con sus preferencias registradas
        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + command.userId()));

        // 2. Obtener preferencias y restricciones del perfil
        List<String> restrictions = user.getRestrictions() != null ? user.getRestrictions() : List.of();
        List<String> preferences = new ArrayList<>(user.getPreferences() != null ? user.getPreferences() : List.of());

        // 3. Enriquecer preferencias con historial de órdenes (últimas 10)
        List<Order> recentOrders = orderRepositoryPort.findByUserId(command.userId())
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.ENTREGADO)
                .filter(o -> o.getCreadoEn() != null &&
                        o.getCreadoEn().isAfter(LocalDateTime.now().minusMonths(3)))
                .sorted((a, b) -> b.getCreadoEn().compareTo(a.getCreadoEn()))
                .limit(10)
                .toList();

        // 4. Extraer nombres de productos pedidos frecuentemente
        if (!recentOrders.isEmpty()) {
            Map<Long, Long> productFrequency = recentOrders.stream()
                    .flatMap(o -> o.getItems() != null ? o.getItems().stream() : java.util.stream.Stream.empty())
                    .collect(Collectors.groupingBy(OrderItem::getProductId, Collectors.counting()));

            // Top 3 productos más pedidos como preferencias dinámicas
            List<Long> topProductIds = productFrequency.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .toList();

            Map<Long, Product> productsById = productRepositoryPort.findAllById(topProductIds).stream()
                    .collect(Collectors.toMap(Product::getId, product -> product));
            topProductIds.stream()
                    .map(productsById::get)
                    .filter(product -> product != null && !preferences.contains(product.getNombre()))
                    .forEach(product -> preferences.add(product.getNombre()));
        }

        // 5. Contexto temporal (mañana/mediodía/tarde)
        int hour = LocalDateTime.now().getHour();
        if (hour >= 6 && hour < 10) {
            preferences.add("desayuno");
        } else if (hour >= 11 && hour < 15) {
            preferences.add("almuerzo");
            if (user.getBudgetRange() != null) preferences.add("presupuesto:" + user.getBudgetRange().name());
        } else if (hour >= 15 && hour < 20) {
            preferences.add("snack");
        }

        // 6. Obtener productos activos disponibles
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

        // 7. Construir request y llamar al microservicio de IA
        AiRecommendationRequest request = AiRecommendationRequest.builder()
                .userId(command.userId())
                .restrictions(restrictions)
                .preferences(preferences)
                .availableProducts(productInfos)
                .maxRecommendations(command.maxRecommendations())
                .build();

        return aiRecommendationPort.getRecommendations(request);
    }
}

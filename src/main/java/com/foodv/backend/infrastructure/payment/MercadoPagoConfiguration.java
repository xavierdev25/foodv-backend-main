package com.foodv.backend.infrastructure.payment;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MercadoPagoConfiguration {

    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accessToken;

    @Value("${MERCADOPAGO_PUBLIC_KEY}")
    private String publicKey;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}

package com.foodv.backend.infrastructure.payment;

import com.mercadopago.MercadoPagoConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MercadoPagoConfiguration {

    @Value("${MERCADOPAGO_ACCESS_TOKEN:placeholder}")
    private String accessToken;

    @Value("${MERCADOPAGO_PUBLIC_KEY:placeholder}")
    private String publicKey;

    @Bean
    public MercadoPagoConfiguration mercadoPagoConfig() {
        MercadoPagoConfig.setAccessToken(accessToken);
        return this;
    }
}

package com.foodv.backend.infrastructure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga el archivo .env del directorio actual y lo agrega como property source de mayor prioridad
 * que application.yaml pero menor que las variables de entorno reales y system properties.
 *
 * Comportamiento:
 *  - addFirst() para que sus claves sobrescriban application.yaml.
 *  - Soporta líneas en blanco, comentarios (#), KEY=VAL.
 *  - Soporta valores entre comillas (simples o dobles) y permite '=' en el valor.
 *  - No falla si .env no existe.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> properties = loadDotenv();
        if (!properties.isEmpty()) {
            environment.getPropertySources().addFirst(
                    new MapPropertySource(PROPERTY_SOURCE_NAME, properties)
            );
        }
    }

    private Map<String, Object> loadDotenv() {
        Map<String, Object> properties = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                int idx = trimmed.indexOf('=');
                if (idx < 0) continue;
                String key = trimmed.substring(0, idx).trim();
                if (key.isEmpty()) continue;
                String value = trimmed.substring(idx + 1).trim();
                value = stripQuotes(value);
                value = stripInlineComment(value);
                properties.put(key, value);
            }
        } catch (IOException e) {
            // .env opcional: nada que hacer
        }
        return properties;
    }

    private String stripQuotes(String value) {
        if (value.length() >= 2) {
            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"))) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    private String stripInlineComment(String value) {
        // No quitar # si el valor venía entre comillas (lo cual ya quitamos arriba),
        // o si estamos seguros que no es un comentario inline.
        int hash = value.indexOf(" #");
        return hash >= 0 ? value.substring(0, hash).trim() : value;
    }
}

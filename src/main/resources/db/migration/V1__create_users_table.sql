CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    nombres       VARCHAR(100)        NOT NULL,
    apellidos     VARCHAR(100)        NOT NULL,
    email         VARCHAR(255)        NOT NULL UNIQUE,
    password      VARCHAR(255)        NOT NULL,
    telefono      VARCHAR(20),
    role          VARCHAR(20)         NOT NULL,
    activo        BOOLEAN             NOT NULL DEFAULT TRUE,
    creado_en     TIMESTAMP WITHOUT TIME ZONE,
    actualizado_en TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

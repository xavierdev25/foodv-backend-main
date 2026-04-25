CREATE TABLE stores (
    id         BIGSERIAL     PRIMARY KEY,
    nombre     VARCHAR(100)  NOT NULL,
    descripcion TEXT,
    imagen_url  VARCHAR(500),
    telefono   VARCHAR(20),
    owner_role VARCHAR(20)   NOT NULL,
    owner_id   BIGINT        NOT NULL REFERENCES users(id),
    activo     BOOLEAN       NOT NULL DEFAULT TRUE,
    creado_en  TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_stores_owner_id ON stores(owner_id);
CREATE INDEX idx_stores_activo ON stores(activo);

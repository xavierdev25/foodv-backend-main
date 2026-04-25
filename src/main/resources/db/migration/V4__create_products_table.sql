CREATE TABLE products (
    id          BIGSERIAL       PRIMARY KEY,
    nombre      VARCHAR(100)    NOT NULL,
    descripcion TEXT,
    precio      NUMERIC(10,2)   NOT NULL,
    stock       INTEGER         NOT NULL DEFAULT 0,
    imagen_url  VARCHAR(500),
    categoria   VARCHAR(20)     NOT NULL,
    store_id    BIGINT          NOT NULL REFERENCES stores(id),
    activo      BOOLEAN         NOT NULL DEFAULT TRUE,
    disponible  BOOLEAN         NOT NULL DEFAULT TRUE,
    creado_en   TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_products_store_id ON products(store_id);
CREATE INDEX idx_products_categoria ON products(categoria);
CREATE INDEX idx_products_activo ON products(activo);

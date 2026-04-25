CREATE TABLE orders (
    id             BIGSERIAL       PRIMARY KEY,
    user_id        BIGINT          NOT NULL REFERENCES users(id),
    store_id       BIGINT          NOT NULL REFERENCES stores(id),
    aula_id        BIGINT          NOT NULL REFERENCES aulas(id),
    total          NUMERIC(10,2)   NOT NULL,
    status         VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    notas          TEXT,
    creado_en      TIMESTAMP WITHOUT TIME ZONE,
    actualizado_en TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE order_items (
    id             BIGSERIAL       PRIMARY KEY,
    order_id       BIGINT          NOT NULL REFERENCES orders(id),
    product_id     BIGINT          NOT NULL REFERENCES products(id),
    product_nombre VARCHAR(100)    NOT NULL,
    product_precio NUMERIC(10,2)   NOT NULL,
    cantidad       INTEGER         NOT NULL,
    subtotal       NUMERIC(10,2)   NOT NULL
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_store_id ON orders(store_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

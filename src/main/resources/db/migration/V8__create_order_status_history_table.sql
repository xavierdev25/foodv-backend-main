CREATE TABLE order_status_history (
    id          BIGSERIAL    PRIMARY KEY,
    order_id    BIGINT       NOT NULL REFERENCES orders(id),
    status      VARCHAR(20)  NOT NULL,
    changed_by  BIGINT       REFERENCES users(id),
    notas       TEXT,
    creado_en   TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id);
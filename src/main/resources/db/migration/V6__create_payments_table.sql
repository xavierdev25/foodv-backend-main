CREATE TABLE payments (
    id             BIGSERIAL       PRIMARY KEY,
    order_id       BIGINT          NOT NULL REFERENCES orders(id),
    user_id        BIGINT          NOT NULL REFERENCES users(id),
    amount         NUMERIC(10,2)   NOT NULL,
    status         VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    external_id    VARCHAR(255),
    payment_url    VARCHAR(500),
    failure_reason TEXT,
    creado_en      TIMESTAMP WITHOUT TIME ZONE,
    actualizado_en TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_external_id ON payments(external_id);
CREATE INDEX idx_payments_status ON payments(status);

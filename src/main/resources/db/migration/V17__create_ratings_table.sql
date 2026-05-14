CREATE TABLE ratings (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT    NOT NULL REFERENCES orders(id),
    user_id     BIGINT    NOT NULL REFERENCES users(id),
    store_id    BIGINT    NOT NULL REFERENCES stores(id),
    rating      INT       NOT NULL,
    comentario  VARCHAR(500),
    creado_en   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ratings_order UNIQUE (order_id),
    CONSTRAINT chk_ratings_rating CHECK (rating >= 1 AND rating <= 5)
);

CREATE INDEX idx_ratings_user_id ON ratings(user_id);
CREATE INDEX idx_ratings_store_id ON ratings(store_id);
CREATE INDEX idx_ratings_creado_en ON ratings(creado_en);

CREATE TABLE favorite_products (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL REFERENCES users(id),
    product_id  BIGINT    NOT NULL REFERENCES products(id),
    creado_en   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_favorite_products_user_product UNIQUE (user_id, product_id)
);

CREATE TABLE favorite_stores (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL REFERENCES users(id),
    store_id    BIGINT    NOT NULL REFERENCES stores(id),
    creado_en   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_favorite_stores_user_store UNIQUE (user_id, store_id)
);

CREATE INDEX idx_favorite_products_user_id ON favorite_products(user_id);
CREATE INDEX idx_favorite_products_product_id ON favorite_products(product_id);
CREATE INDEX idx_favorite_stores_user_id ON favorite_stores(user_id);
CREATE INDEX idx_favorite_stores_store_id ON favorite_stores(store_id);

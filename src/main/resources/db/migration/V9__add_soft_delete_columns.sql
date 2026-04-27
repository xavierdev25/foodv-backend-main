ALTER TABLE users ADD COLUMN deleted_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL;
ALTER TABLE products ADD COLUMN deleted_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL;
ALTER TABLE stores ADD COLUMN deleted_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL;

CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_products_deleted_at ON products(deleted_at);
CREATE INDEX idx_stores_deleted_at ON stores(deleted_at);
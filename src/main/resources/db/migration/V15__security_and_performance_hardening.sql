-- =====================================================
-- V15: Hardening de seguridad y performance.
--   * Indices para queries frecuentes (orders, payments, products, refresh_tokens).
--   * Constraints únicos faltantes (refresh_tokens.token, payments.external_id).
--   * Columna actualizado_en para entidades que ahora la usan (stores, products).
--   * Indice parcial para soft-delete en users/products/stores.
-- =====================================================

-- stores: actualizado_en
ALTER TABLE stores
    ADD COLUMN IF NOT EXISTS actualizado_en TIMESTAMP;

-- products: actualizado_en
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS actualizado_en TIMESTAMP;

-- ============================
-- ÍNDICES PARA ORDERS
-- ============================
CREATE INDEX IF NOT EXISTS idx_orders_user_status_creado
    ON orders (user_id, status, creado_en DESC);

CREATE INDEX IF NOT EXISTS idx_orders_store_status_creado
    ON orders (store_id, status, creado_en DESC);

CREATE INDEX IF NOT EXISTS idx_orders_status_creado
    ON orders (status, creado_en DESC);

CREATE INDEX IF NOT EXISTS idx_orders_repartidor_status
    ON orders (repartidor_id, status)
    WHERE repartidor_id IS NOT NULL;

-- ============================
-- ÍNDICES PARA PAYMENTS
-- ============================
CREATE INDEX IF NOT EXISTS idx_payments_user_creado
    ON payments (user_id, creado_en DESC);

CREATE INDEX IF NOT EXISTS idx_payments_order
    ON payments (order_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_payments_external_id
    ON payments (external_id)
    WHERE external_id IS NOT NULL;

-- ============================
-- ÍNDICES PARA PRODUCTS
-- ============================
CREATE INDEX IF NOT EXISTS idx_products_store_active_visible
    ON products (store_id, activo, disponible)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_products_categoria_active
    ON products (categoria, activo)
    WHERE deleted_at IS NULL;

-- ============================
-- ÍNDICES PARA USERS
-- ============================
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email_alive
    ON users (LOWER(email))
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_users_role_active
    ON users (role, activo)
    WHERE deleted_at IS NULL;

-- ============================
-- ÍNDICES PARA STORES
-- ============================
CREATE INDEX IF NOT EXISTS idx_stores_owner_active
    ON stores (owner_id, activo)
    WHERE deleted_at IS NULL;

-- ============================
-- ÍNDICES PARA REFRESH_TOKENS
-- ============================
CREATE UNIQUE INDEX IF NOT EXISTS uq_refresh_tokens_token
    ON refresh_tokens (token);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_active
    ON refresh_tokens (user_id)
    WHERE revoked = FALSE;

-- ============================
-- ÍNDICES PARA ORDER_STATUS_HISTORY
-- ============================
CREATE INDEX IF NOT EXISTS idx_order_status_history_order_creado
    ON order_status_history (order_id, creado_en);

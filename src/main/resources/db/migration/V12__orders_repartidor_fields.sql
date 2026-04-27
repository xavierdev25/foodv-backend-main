-- Nuevos campos en orders
ALTER TABLE orders ADD COLUMN repartidor_id BIGINT REFERENCES users(id);
ALTER TABLE orders ADD COLUMN motivo_cancelacion TEXT;
ALTER TABLE orders ADD COLUMN cancelado_por BIGINT REFERENCES users(id);
ALTER TABLE orders ADD COLUMN propina NUMERIC(10,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN tarifa_servicio NUMERIC(10,2) DEFAULT 1.50;
ALTER TABLE orders ADD COLUMN comision_foodv NUMERIC(10,2) DEFAULT 0.50;
ALTER TABLE orders ADD COLUMN codigo_confirmacion VARCHAR(4);
ALTER TABLE orders ADD COLUMN foto_entrega_url TEXT;

-- Roles múltiples en users
ALTER TABLE users ADD COLUMN es_repartidor BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN campus_id BIGINT;

CREATE INDEX idx_orders_repartidor_id ON orders(repartidor_id);
CREATE INDEX idx_orders_cancelado_por ON orders(cancelado_por);
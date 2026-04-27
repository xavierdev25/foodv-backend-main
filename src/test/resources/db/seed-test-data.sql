-- Seed mínimo para tests de integración
-- Solo se ejecuta en el perfil "test"
INSERT INTO users (nombres, apellidos, email, password, role, activo, creado_en)
VALUES ('Test', 'Owner', 'owner@test.com', 'hashed', 'COMERCIO', true, NOW())
    ON CONFLICT (email) DO NOTHING;

INSERT INTO stores (nombre, descripcion, owner_id, owner_role, activo, creado_en)
SELECT 'Tienda Test CI', 'Store para tests', id, 'COMERCIO', true, NOW()
FROM users WHERE email = 'owner@test.com'
    ON CONFLICT DO NOTHING;
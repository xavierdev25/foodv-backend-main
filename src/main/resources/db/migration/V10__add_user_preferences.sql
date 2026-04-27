ALTER TABLE users
    ADD COLUMN IF NOT EXISTS preferences TEXT[] DEFAULT '{}',
    ADD COLUMN IF NOT EXISTS restrictions TEXT[] DEFAULT '{}',
    ADD COLUMN IF NOT EXISTS budget_range VARCHAR(20) DEFAULT 'MEDIO',
    ADD COLUMN IF NOT EXISTS cuisine_types TEXT[] DEFAULT '{}';

COMMENT ON COLUMN users.preferences IS 'Gustos de comida: ej. pollo, arroz, pasta';
COMMENT ON COLUMN users.restrictions IS 'Restricciones: VEGETARIANO, VEGANO, SIN_GLUTEN, SIN_LACTOSA';
COMMENT ON COLUMN users.budget_range IS 'BAJO (<5 soles), MEDIO (5-15 soles), ALTO (>15 soles)';
COMMENT ON COLUMN users.cuisine_types IS 'Tipos de cocina: criolla, italiana, china, americana';
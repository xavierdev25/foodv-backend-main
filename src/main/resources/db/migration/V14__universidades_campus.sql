CREATE TABLE universidades (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(200) NOT NULL,
    pais        VARCHAR(100) NOT NULL DEFAULT 'Perú',
    ciudad      VARCHAR(100) NOT NULL,
    activo      BOOLEAN DEFAULT TRUE,
    creado_en   TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE campus (
    id                BIGSERIAL PRIMARY KEY,
    universidad_id    BIGINT NOT NULL REFERENCES universidades(id),
    nombre            VARCHAR(200) NOT NULL,
    direccion         TEXT,
    activo            BOOLEAN DEFAULT TRUE,
    creado_en         TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE campus_mapas (
    id              BIGSERIAL PRIMARY KEY,
    campus_id       BIGINT NOT NULL UNIQUE REFERENCES campus(id),
    centro_lat      NUMERIC(10,7) NOT NULL,
    centro_lng      NUMERIC(10,7) NOT NULL,
    zoom_default    INTEGER DEFAULT 17,
    bounds          JSONB,
    zonas           JSONB,
    rutas           JSONB,
    puntos_entrega  JSONB,
    activo          BOOLEAN DEFAULT TRUE,
    actualizado_en  TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE puntos_entrega (
    id          BIGSERIAL PRIMARY KEY,
    campus_id   BIGINT NOT NULL REFERENCES campus(id),
    nombre      VARCHAR(200) NOT NULL,
    descripcion TEXT,
    lat         NUMERIC(10,7) NOT NULL,
    lng         NUMERIC(10,7) NOT NULL,
    tipo        VARCHAR(30) DEFAULT 'EDIFICIO',
    activo      BOOLEAN DEFAULT TRUE
);

-- Insertar UCV Lima Norte como primer cliente
INSERT INTO universidades (nombre, pais, ciudad) VALUES ('Universidad César Vallejo', 'Perú', 'Lima');
INSERT INTO campus (universidad_id, nombre, direccion) VALUES (1, 'Lima Norte', 'Av. Alfredo Mendiola 6232, Los Olivos');

CREATE INDEX idx_campus_universidad_id ON campus(universidad_id);
CREATE INDEX idx_puntos_entrega_campus_id ON puntos_entrega(campus_id);
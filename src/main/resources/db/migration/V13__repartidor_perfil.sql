CREATE TABLE repartidor_perfiles (
    id                          BIGSERIAL PRIMARY KEY,
    user_id                     BIGINT NOT NULL UNIQUE REFERENCES users(id),
    dni                         VARCHAR(20) NOT NULL,
    foto_rostro_url             TEXT,
    foto_dni_frontal_url        TEXT,
    foto_dni_posterior_url      TEXT,
    contacto_emergencia_nombre  VARCHAR(100),
    contacto_emergencia_telefono VARCHAR(20),
    estado_verificacion         VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo              TEXT,
    verificado_por              BIGINT REFERENCES users(id),
    verificado_en               TIMESTAMP WITHOUT TIME ZONE,
    activo                      BOOLEAN DEFAULT FALSE,
    rating                      NUMERIC(3,2) DEFAULT 5.00,
    total_entregas              INTEGER DEFAULT 0,
    total_cancelaciones         INTEGER DEFAULT 0,
    creado_en                   TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE calificaciones_repartidor (
    id              BIGSERIAL PRIMARY KEY,
    repartidor_id   BIGINT NOT NULL REFERENCES users(id),
    orden_id        BIGINT NOT NULL REFERENCES orders(id),
    calificador_id  BIGINT NOT NULL REFERENCES users(id),
    rating          INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comentario      TEXT,
    creado_en       TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT uq_calificacion_orden UNIQUE (orden_id, calificador_id)
);

CREATE INDEX idx_repartidor_perfiles_user_id ON repartidor_perfiles(user_id);
CREATE INDEX idx_repartidor_perfiles_estado ON repartidor_perfiles(estado_verificacion);
CREATE INDEX idx_calificaciones_repartidor_id ON calificaciones_repartidor(repartidor_id);
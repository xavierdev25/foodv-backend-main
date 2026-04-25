CREATE TABLE aulas (
    id       BIGSERIAL    PRIMARY KEY,
    codigo   VARCHAR(20)  NOT NULL UNIQUE,
    nombre   VARCHAR(100) NOT NULL,
    piso     VARCHAR(20),
    pabellon VARCHAR(50),
    activo   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_aulas_codigo ON aulas(codigo);
CREATE INDEX idx_aulas_activo ON aulas(activo);

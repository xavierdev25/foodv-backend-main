ALTER TABLE stores ADD COLUMN horario_apertura TIME DEFAULT '08:00:00';
ALTER TABLE stores ADD COLUMN horario_cierre TIME DEFAULT '20:00:00';

UPDATE stores
SET horario_apertura = '07:30:00',
    horario_cierre = '20:00:00'
WHERE id IN (8,9,10,11,12,13);

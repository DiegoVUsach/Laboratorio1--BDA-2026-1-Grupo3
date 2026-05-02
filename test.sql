-- Trigger 2 probar. (Comprobar ids, quiza falla por eso, aunque quiza no debe hacerse
-- desde postgres, sino desde postman)

-- Cambiemos el líder (asumiendo que tienes otro personaje con ID 2)
UPDATE clanes SET id_lider = 2 WHERE id_clan = 2;

-- Ver la auditoría
SELECT * FROM auditoria_liderazgo;

-- Trigger 1

-- 1. Crear una Raid de alto nivel
INSERT INTO raids (nombre_raid, fecha_raid, item_level_minimo, tanques, healers, dps)
VALUES ('Sagrario de la Oscuridad', '2026-06-01 20:00:00', 500, 2, 5, 18);

-- 2. Intentar inscribir a Jaina (tiene ilvl 450, la raid pide 500)
-- ESTO DEBERÍA LANZAR UN ERROR DE POSTGRES
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid) VALUES (1, 1, 'DPS');


-- Repartir botin Procedimiento almacenado 1

-- 1. Crear un Item
INSERT INTO item (nombre_item, costo_dkp) VALUES ('Vara de Archidruida', 200);

-- 2. Ejecutar el procedimiento
-- (id_personaje: 1, id_item: 1, id_raid: 1)
CALL sp_repartir_botin(1, 1, 1);

-- 3. Verificar que bajaron los DKP y se guardó el historial
SELECT nombre_personaje, puntos_dkp_actuales FROM personaje WHERE id_personaje = 1;
SELECT * FROM historial_botin;

--Ranking Vista materializada
-- Ver el ranking actual
SELECT * FROM vista_ranking_clan;

-- Si agregas personajes y quieres actualizar el ranking:
REFRESH MATERIALIZED VIEW vista_ranking_clan;
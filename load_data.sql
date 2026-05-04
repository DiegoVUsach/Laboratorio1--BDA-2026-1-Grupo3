-- ============================================================
-- DATOS DE PRUEBA (Seeders)
-- ============================================================

-- Usuarios (passwords hasheadas con BCrypt - todas son '123456')
INSERT INTO usuario (nombre_usuario, password, rol) VALUES ('admin', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'ADMIN');
INSERT INTO usuario (nombre_usuario, password, rol) VALUES ('jugador1', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER');
INSERT INTO usuario (nombre_usuario, password, rol) VALUES ('jugador2', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER');
INSERT INTO usuario (nombre_usuario, password, rol) VALUES ('jugador3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER');

-- Clanes
INSERT INTO clanes (nombre_clan) VALUES ('Los Heraldos de Azeroth');
INSERT INTO clanes (nombre_clan) VALUES ('Guardianes del Alba');

-- Personajes del admin (id_usuario=1) - es Guild Master del clan 1
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales)
VALUES (1, 1, 'ArthasPvP', 'Paladin', 'Los Primordiales de la Luz', 'Guild Master', 60, 250, 1000);

-- Personajes del jugador1 (id_usuario=2) - un Raider y un Member
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales)
VALUES (2, 1, 'JainaFrost', 'Mago', 'Los Hijos del Gris', 'Raider', 60, 245, 800);

INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales)
VALUES (2, 1, 'ThrallShaman', 'Chaman', 'Los Marcados por el Abismo', 'Member', 55, 200, 400);

-- Personajes del jugador2 (id_usuario=3) - Raider del clan 1
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales)
VALUES (3, 1, 'SylvanasArcher', 'Cazador', 'Los Hijos del Gris', 'Raider', 58, 240, 750);

-- Personajes del jugador3 (id_usuario=4) - Guild Master del clan 2
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales)
VALUES (4, 2, 'VoljinShadow', 'Brujo', 'Los Primordiales de la Luz', 'Guild Master', 60, 230, 600);

INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales)
VALUES (4, 2, 'GarroshWarrior', 'Guerrero', 'Los Marcados por el Abismo', 'Raider', 57, 220, 500);

-- Asignar lideres a los clanes (cierra la referencia circular)
UPDATE clanes SET id_lider = 1 WHERE id_clan = 1;
UPDATE clanes SET id_lider = 5 WHERE id_clan = 2;

-- Items de botin
-- Items de botin con niveles de item reales para hacer raids
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Agonia de Escarcha', 'Legendario', 'ARMA', 280, 500);
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Escudo de Lordaeron', 'Epico', 'ARMADURA', 250, 200);
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Capa de las Sombras', 'Raro', 'ACCESORIO', 230, 100);
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Anillo del Destino', 'Epico', 'ACCESORIO', 245, 300);
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Botas de Viento', 'Comun', 'ARMADURA', 210, 50);
-- Inventario de prueba
INSERT INTO inventario (id_personaje, armadura_equipado, arma_equipado, accesorio_equipado)
VALUES (1, 2, 1, 4);

INSERT INTO inventario (id_personaje, armadura_equipado, arma_equipado, accesorio_equipado)
VALUES (2, 5, 1, 3);

INSERT INTO inventario (id_personaje, armadura_equipado, arma_equipado, accesorio_equipado)
VALUES (3, NULL, 1, 4);

-- Items dentro del inventario
INSERT INTO inventario_item (id_inventario, id_item) VALUES (1, 1);
INSERT INTO inventario_item (id_inventario, id_item) VALUES (1, 2);
INSERT INTO inventario_item (id_inventario, id_item) VALUES (1, 4);
INSERT INTO inventario_item (id_inventario, id_item) VALUES (2, 1);
INSERT INTO inventario_item (id_inventario, id_item) VALUES (2, 3);
INSERT INTO inventario_item (id_inventario, id_item) VALUES (3, 4);

-- Raids programadas
-- Raids programadas
INSERT INTO raids (nombre_raid, fecha_raid, item_level_minimo, tanques, healers, dps, estado)
VALUES ('ICC 25H', '2026-05-10 20:00:00', 240, 2, 5, 18, 'PROGRAMADA');

INSERT INTO raids (nombre_raid, fecha_raid, item_level_minimo, tanques, healers, dps, estado)
VALUES ('Ulduar 10N', '2026-05-12 19:00:00', 200, 2, 3, 5, 'PROGRAMADA');

INSERT INTO raids (nombre_raid, fecha_raid, item_level_minimo, tanques, healers, dps, estado)
VALUES ('Naxxramas 25H', '2026-05-08 21:00:00', 220, 2, 6, 17, 'PROGRAMADA');

-- Inscripciones de prueba
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado)
VALUES (1, 1, 'TANQUE', TRUE);
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado)
VALUES (1, 2, 'DPS', TRUE);
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado)
VALUES (1, 4, 'DPS', FALSE);
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado)
VALUES (2, 1, 'TANQUE', TRUE);
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado)
VALUES (2, 3, 'HEALER', TRUE);

-- Historial de botin de prueba
INSERT INTO historial_botin (id_personaje, id_item, id_raid) VALUES (1, 2, 1);
INSERT INTO historial_botin (id_personaje, id_item, id_raid) VALUES (2, 3, 1);

-- Refrescar la vista materializada con los datos cargados
SELECT refrescar_ranking_clan();
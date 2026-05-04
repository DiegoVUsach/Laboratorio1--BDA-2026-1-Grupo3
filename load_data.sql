-- ============================================================
-- DATOS DE PRUEBA (Seeders)
-- ============================================================

-- Usuarios (password en texto plano por simplicidad)
INSERT INTO usuario (nombre_usuario, password, rol) VALUES ('admin', '123456', 'ADMIN');
INSERT INTO usuario (nombre_usuario, password, rol) VALUES ('jugador1', '123456', 'USER');
INSERT INTO usuario (nombre_usuario, password, rol) VALUES ('jugador2', '123456', 'USER');
INSERT INTO usuario (nombre_usuario, password, rol) VALUES ('jugador3', '123456', 'USER');

-- Clanes
INSERT INTO clanes (nombre_clan) VALUES ('Los Heraldos de Azeroth');
INSERT INTO clanes (nombre_clan) VALUES ('Guardianes del Alba');

-- Personajes del admin (id_usuario=1) - es Guild Master del clan 1
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, item_level, puntos_dkp_actuales)
VALUES (1, 1, 'ArthasPvP', 'Paladin', 'Los Primordiales de la Luz', 'Guild Master', 250, 1000);

-- Personajes del jugador1 (id_usuario=2) - un Raider y un Member
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, item_level, puntos_dkp_actuales)
VALUES (2, 1, 'JainaFrost', 'Mago', 'Los Hijos del Gris', 'Raider', 245, 800);

INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, item_level, puntos_dkp_actuales)
VALUES (2, 1, 'ThrallShaman', 'Chaman', 'Los Marcados por el Abismo', 'Member', 200, 400);

-- Personajes del jugador2 (id_usuario=3) - Raider del clan 1
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, item_level, puntos_dkp_actuales)
VALUES (3, 1, 'SylvanasArcher', 'Cazador', 'Los Hijos del Gris', 'Raider', 240, 750);

-- Personajes del jugador3 (id_usuario=4) - Guild Master del clan 2
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, item_level, puntos_dkp_actuales)
VALUES (4, 2, 'VoljinShadow', 'Brujo', 'Los Primordiales de la Luz', 'Guild Master', 230, 600);

INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, item_level, puntos_dkp_actuales)
VALUES (4, 2, 'GarroshWarrior', 'Guerrero', 'Los Marcados por el Abismo', 'Raider', 220, 500);

-- Asignar lideres a los clanes (cierra la referencia circular)
UPDATE clanes SET id_lider = 1 WHERE id_clan = 1;
UPDATE clanes SET id_lider = 5 WHERE id_clan = 2;

-- Items de botin
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Agonia de Escarcha', 'Legendario', 'ARMA', 60, 500);
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Escudo de Lordaeron', 'Epico', 'ARMADURA', 50, 200);
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Capa de las Sombras', 'Raro', 'ACCESORIO', 45, 100);
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Anillo del Destino', 'Epico', 'ACCESORIO', 55, 300);
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES ('Botas de Viento', 'Comun', 'ARMADURA', 35, 50);

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
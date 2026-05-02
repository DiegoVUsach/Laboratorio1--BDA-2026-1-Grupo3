-- 1. Insertar un Usuario
INSERT INTO usuario (nombre_usuario, password) VALUES ('admin', '123456');

-- 2. Insertar un Clan
INSERT INTO clanes (nombre_clan) VALUES ('Los Heraldos de Azeroth');

-- 3. Insertar Personaje (El Líder)
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, rol_clan, nivel, item_level, puntos_dkp_actuales) 
VALUES (1, 1, 'ArthasPvP', 'Paladín', 'Guild Master', 60, 250, 1000);

-- 4. Asignar el líder al clan (Cerramos el círculo)
UPDATE clanes SET id_lider = 1 WHERE id_clan = 1;

-- 5. Insertar algunos Items
INSERT INTO item (nombre_item, costo_dkp) VALUES ('Agonía de Escarcha', 500);
INSERT INTO item (nombre_item, costo_dkp) VALUES ('Escudo de Lordaeron', 200);

-- 6. Insertar una Raid de prueba
INSERT INTO raids (nombre_raid, fecha_raid, item_level_minimo, tanques, healers, dps) 
VALUES ('ICC 25H', '2026-05-15 20:00:00', 240, 2, 5, 18);
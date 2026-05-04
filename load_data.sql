-- ============================================================
-- DATOS DE PRUEBA (Seeders)
-- Grupo 3: Gestor de Clanes y Raids para MMORPG
-- ============================================================
-- REGLAS RESPETADAS:
--   - Todos los personajes de un mismo clan comparten faccion (trg_validar_faccion_clan)
--   - Inscripciones solo si item_level >= item_level_minimo de la raid (trg_validar_item_level)
--   - item_level se calcula automaticamente al equipar items (trg_recalcular_item_level)
--   - No hay duplicados de inscripcion por (id_raid, id_personaje) (uq_inscripcion_unica)
--   - Personajes sin clan no pueden ser Raider ni Guild Master (funcion_validar_rol_sin_clan)
-- ============================================================

-- ============================================================
-- 1. USUARIOS (8 usuarios — password: '123456' hasheada con BCrypt)
-- ============================================================
INSERT INTO usuario (nombre_usuario, password, rol) VALUES
('admin',    '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'ADMIN'),
('jugador1', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER'),
('jugador2', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER'),
('jugador3', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER'),
('jugador4', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER'),
('jugador5', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER'),
('jugador6', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER'),
('jugador7', '$2a$10$sryR9keWKkegImZmgjLz4.xs1UyF4Z8dzbqVaSzlea2D7UaRJ50b2', 'USER');

-- IDs resultantes: admin=1, jugador1=2, jugador2=3, jugador3=4,
--                  jugador4=5, jugador5=6, jugador6=7, jugador7=8

-- ============================================================
-- 2. CLANES (3 clanes, uno por faccion)
-- ============================================================
INSERT INTO clanes (nombre_clan) VALUES
('Los Heraldos de Azeroth'),    -- id_clan=1, faccion: Los Primordiales de la Luz
('Sombras del Crepusculo'),     -- id_clan=2, faccion: Los Hijos del Gris
('Legión del Abismo Eterno');   -- id_clan=3, faccion: Los Marcados por el Abismo

-- ============================================================
-- 3. PERSONAJES (15 personajes distribuidos en 3 clanes + 1 sin clan)
-- ============================================================
-- Todos entran con item_level=0, el trigger lo recalcula al equipar.
-- Cada clan tiene UNA sola faccion (trg_validar_faccion_clan).

-- === CLAN 1: Los Heraldos de Azeroth (Los Primordiales de la Luz) ===
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales) VALUES
(1, 1, 'ArthasPvP',       'Paladin',    'Los Primordiales de la Luz', 'Guild Master', 60, 0, 1200),  -- id=1
(2, 1, 'JainaFrost',      'Mago',       'Los Primordiales de la Luz', 'Raider',       58, 0,  850),  -- id=2
(2, 1, 'ThrallShaman',    'Chaman',     'Los Primordiales de la Luz', 'Member',       45, 0,  400),  -- id=3
(3, 1, 'SylvanasArcher',  'Cazador',    'Los Primordiales de la Luz', 'Raider',       55, 0,  780),  -- id=4
(5, 1, 'UtherSanador',    'Sacerdote',  'Los Primordiales de la Luz', 'Raider',       57, 0,  720),  -- id=5
(6, 1, 'TiraelGuardia',   'Guerrero',   'Los Primordiales de la Luz', 'Raider',       53, 0,  650);  -- id=6

-- === CLAN 2: Sombras del Crepusculo (Los Hijos del Gris) ===
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales) VALUES
(4, 2, 'VoljinShadow',    'Brujo',      'Los Hijos del Gris', 'Guild Master', 60, 0, 900),  -- id=7
(4, 2, 'GarroshWarrior',  'Guerrero',   'Los Hijos del Gris', 'Raider',       56, 0, 550),  -- id=8
(7, 2, 'MedivhArcano',    'Mago',       'Los Hijos del Gris', 'Raider',       59, 0, 810),  -- id=9
(7, 2, 'KhadgarSabio',    'Sacerdote',  'Los Hijos del Gris', 'Member',       48, 0, 300);  -- id=10

-- === CLAN 3: Legión del Abismo Eterno (Los Marcados por el Abismo) ===
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales) VALUES
(8, 3, 'IllidanFuria',    'Picaro',     'Los Marcados por el Abismo', 'Guild Master', 60, 0, 1100),  -- id=11
(8, 3, 'GuldanOscuro',    'Brujo',      'Los Marcados por el Abismo', 'Raider',       54, 0,  620),  -- id=12
(6, 3, 'MalfurionDruid',  'Chaman',     'Los Marcados por el Abismo', 'Raider',       56, 0,  690);  -- id=13

-- === SIN CLAN (jugadores nuevos o expulsados) ===
INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, faccion, rol_clan, nivel, item_level, puntos_dkp_actuales) VALUES
(5, NULL, 'RoninErrante',  'Cazador',   'Los Hijos del Gris',         'Member',       30, 0, 100),   -- id=14
(3, NULL, 'NoviceHero',    'Guerrero',  'Los Primordiales de la Luz',  'Member',       10, 0,  50);   -- id=15

-- ============================================================
-- 4. ASIGNAR LIDERES DE CLAN
-- ============================================================
-- Esto dispara trg_auditoria_liderazgo automaticamente.
UPDATE clanes SET id_lider = 1  WHERE id_clan = 1;  -- ArthasPvP lidera Heraldos
UPDATE clanes SET id_lider = 7  WHERE id_clan = 2;  -- VoljinShadow lidera Sombras
UPDATE clanes SET id_lider = 11 WHERE id_clan = 3;  -- IllidanFuria lidera Legion

-- ============================================================
-- 5. ITEMS DE BOTIN (15 items variados)
-- ============================================================
-- Niveles pensados para que el promedio de 3 items equipados
-- supere (o no) los item_level_minimo de las raids.

-- Armas (5)
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES
('Agonia de Escarcha',       'Legendario', 'ARMA', 280, 500),   -- id=1
('Filo de Tormenta',         'Epico',      'ARMA', 260, 350),   -- id=2
('Daga del Vacio',           'Raro',       'ARMA', 230, 180),   -- id=3
('Baculo del Arcano',        'Epico',      'ARMA', 270, 400),   -- id=4
('Hacha de Leñador',         'Comun',      'ARMA', 150, 40);    -- id=5

-- Armaduras (5)
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES
('Pechera de Lordaeron',     'Epico',      'ARMADURA', 260, 300),  -- id=6
('Tunica de Telar Arcano',   'Raro',       'ARMADURA', 240, 150),  -- id=7
('Coraza del Berserker',     'Legendario', 'ARMADURA', 290, 550),  -- id=8
('Cota de Mallas Oxidada',   'Comun',      'ARMADURA', 160, 30),   -- id=9
('Vestiduras de las Sombras','Epico',      'ARMADURA', 250, 280);  -- id=10

-- Accesorios (5)
INSERT INTO item (nombre_item, rareza, tipo, nivel, costo_dkp) VALUES
('Anillo del Destino',       'Epico',      'ACCESORIO', 250, 300), -- id=11
('Capa de las Sombras',      'Raro',       'ACCESORIO', 235, 120), -- id=12
('Amuleto de Fuego Solar',   'Legendario', 'ACCESORIO', 275, 480), -- id=13
('Trinket de Novato',        'Comun',      'ACCESORIO', 140, 25),  -- id=14
('Pendiente del Oráculo',    'Epico',      'ACCESORIO', 255, 320); -- id=15

-- ============================================================
-- 6. INVENTARIOS Y EQUIPAMIENTO
-- ============================================================
-- Al insertar aqui, trg_recalcular_item_level se dispara automaticamente
-- y actualiza el item_level del personaje. Los promedios estan calculados
-- para que algunos personajes cumplan y otros NO cumplan los minimos de las raids.

-- Clan 1: Los Heraldos de Azeroth
INSERT INTO inventario (id_personaje, armadura_equipado, arma_equipado, accesorio_equipado) VALUES
(1, 8,    1,    13),   -- ArthasPvP:      (290+280+275)/3 = 281  → pasa ICC(240), Ulduar(200), Naxx(220)
(2, 7,    4,    11),   -- JainaFrost:     (240+270+250)/3 = 253  → pasa ICC(240)
(3, 9,    5,    14),   -- ThrallShaman:   (160+150+140)/3 = 150  → NO pasa ninguna raid de alto nivel
(4, 6,    2,    12),   -- SylvanasArcher: (260+260+235)/3 = 251  → pasa ICC(240)
(5, 10,   4,    15),   -- UtherSanador:   (250+270+255)/3 = 258  → pasa ICC(240)
(6, 7,    3,    14);   -- TiraelGuardia:  (240+230+140)/3 = 203  → pasa Ulduar(200), NO pasa ICC(240)

-- Clan 2: Sombras del Crepusculo
INSERT INTO inventario (id_personaje, armadura_equipado, arma_equipado, accesorio_equipado) VALUES
(7,  6,   4,    11),   -- VoljinShadow:   (260+270+250)/3 = 260  → pasa ICC(240)
(8,  10,  NULL, 12),   -- GarroshWarrior: (250+235)/2     = 242  → pasa ICC(240) por poco
(9,  8,   1,    15),   -- MedivhArcano:   (290+280+255)/3 = 275  → pasa ICC(240)
(10, 9,   5,    14);   -- KhadgarSabio:   (160+150+140)/3 = 150  → NO pasa raids altas

-- Clan 3: Legión del Abismo Eterno
INSERT INTO inventario (id_personaje, armadura_equipado, arma_equipado, accesorio_equipado) VALUES
(11, 8,   1,    13),   -- IllidanFuria:    (290+280+275)/3 = 281  → pasa ICC(240)
(12, 7,   3,    12),   -- GuldanOscuro:    (240+230+235)/3 = 235  → NO pasa ICC(240), pasa Ulduar/Naxx
(13, 6,   2,    15);   -- MalfurionDruid:  (260+260+255)/3 = 258  → pasa ICC(240)

-- Sin clan (equipo basico)
INSERT INTO inventario (id_personaje, armadura_equipado, arma_equipado, accesorio_equipado) VALUES
(14, 9,   5,    14),   -- RoninErrante: (160+150+140)/3 = 150  → equipo de novato
(15, NULL, 5,   NULL); -- NoviceHero:   150/1           = 150  → recien empieza

-- ============================================================
-- 7. ITEMS EN INVENTARIO (bolsa de cada personaje)
-- ============================================================
-- Registra que items posee cada inventario, incluyendo los equipados
-- y otros adicionales que el personaje tiene guardados.

-- Inventario 1 (ArthasPvP): equipados + items extra en bolsa
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(1, 8), (1, 1), (1, 13),   -- equipados
(1, 6), (1, 3);             -- extras en bolsa

-- Inventario 2 (JainaFrost)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(2, 7), (2, 4), (2, 11),
(2, 12);

-- Inventario 3 (ThrallShaman)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(3, 9), (3, 5), (3, 14);

-- Inventario 4 (SylvanasArcher)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(4, 6), (4, 2), (4, 12),
(4, 15);

-- Inventario 5 (UtherSanador)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(5, 10), (5, 4), (5, 15);

-- Inventario 6 (TiraelGuardia)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(6, 7), (6, 3), (6, 14);

-- Inventario 7 (VoljinShadow)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(7, 6), (7, 4), (7, 11);

-- Inventario 8 (GarroshWarrior)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(8, 10), (8, 12);

-- Inventario 9 (MedivhArcano)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(9, 8), (9, 1), (9, 15),
(9, 7);

-- Inventario 10 (KhadgarSabio)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(10, 9), (10, 5), (10, 14);

-- Inventario 11 (IllidanFuria)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(11, 8), (11, 1), (11, 13),
(11, 2), (11, 11);

-- Inventario 12 (GuldanOscuro)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(12, 7), (12, 3), (12, 12);

-- Inventario 13 (MalfurionDruid)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(13, 6), (13, 2), (13, 15);

-- Inventario 14 (RoninErrante)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(14, 9), (14, 5), (14, 14);

-- Inventario 15 (NoviceHero)
INSERT INTO inventario_item (id_inventario, id_item) VALUES
(15, 5);

-- ============================================================
-- 8. RAIDS PROGRAMADAS (5 raids con distintos niveles de dificultad)
-- ============================================================
INSERT INTO raids (nombre_raid, fecha_raid, item_level_minimo, tanques, healers, dps, estado) VALUES
('Ciudadela de la Corona de Hielo 25H', '2026-05-10 20:00:00', 240, 2, 5, 18, 'PROGRAMADA'),     -- id=1, la mas dificil
('Ulduar 10 Normal',                    '2026-05-12 19:00:00', 200, 2, 3,  5, 'PROGRAMADA'),     -- id=2, accesible
('Naxxramas 25 Heroico',                '2026-05-08 21:00:00', 220, 2, 6, 17, 'PROGRAMADA'),     -- id=3, intermedia
('Templo Oscuro 10H',                   '2026-05-15 20:30:00', 250, 2, 4, 14, 'PROGRAMADA'),     -- id=4, mas dificil que ICC
('Cavernas del Tiempo 10N',             '2026-05-18 18:00:00', 140, 1, 2,  7, 'PROGRAMADA');     -- id=5, para novatos

-- ============================================================
-- 9. INSCRIPCIONES A RAIDS
-- ============================================================
-- Solo se inscriben personajes cuyo item_level calculado (por el trigger)
-- es >= al item_level_minimo de la raid correspondiente.
--
-- Recordatorio de item_levels:
--   ArthasPvP=281, JainaFrost=253, ThrallShaman=150, SylvanasArcher=251,
--   UtherSanador=258, TiraelGuardia=203, VoljinShadow=260, GarroshWarrior=242,
--   MedivhArcano=275, KhadgarSabio=150, IllidanFuria=281, GuldanOscuro=235,
--   MalfurionDruid=258, RoninErrante=150, NoviceHero=150

-- Raid 1: ICC 25H (min 240) — los mejores de cada clan
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado) VALUES
(1, 1,  'TANQUE', TRUE),    -- ArthasPvP (281)
(1, 2,  'DPS',    TRUE),    -- JainaFrost (253)
(1, 4,  'DPS',    TRUE),    -- SylvanasArcher (251)
(1, 5,  'HEALER', TRUE),    -- UtherSanador (258)
(1, 7,  'DPS',    FALSE),   -- VoljinShadow (260) - pendiente confirmacion
(1, 8,  'DPS',    FALSE),   -- GarroshWarrior (242)
(1, 9,  'DPS',    TRUE),    -- MedivhArcano (275)
(1, 11, 'TANQUE', TRUE),    -- IllidanFuria (281)
(1, 13, 'DPS',    FALSE);   -- MalfurionDruid (258)

-- Raid 2: Ulduar 10N (min 200) — raid accesible, incluso TiraelGuardia puede
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado) VALUES
(2, 1,  'TANQUE', TRUE),    -- ArthasPvP (281)
(2, 6,  'DPS',    TRUE),    -- TiraelGuardia (203)
(2, 4,  'DPS',    TRUE),    -- SylvanasArcher (251)
(2, 5,  'HEALER', TRUE),    -- UtherSanador (258)
(2, 8,  'DPS',    FALSE);   -- GarroshWarrior (242)

-- Raid 3: Naxxramas 25H (min 220) — intermedia
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado) VALUES
(3, 1,  'TANQUE', TRUE),    -- ArthasPvP (281)
(3, 2,  'DPS',    TRUE),    -- JainaFrost (253)
(3, 9,  'DPS',    TRUE),    -- MedivhArcano (275)
(3, 11, 'TANQUE', TRUE),    -- IllidanFuria (281)
(3, 12, 'DPS',    TRUE),    -- GuldanOscuro (235) — 235 >= 220 OK
(3, 7,  'DPS',    TRUE),    -- VoljinShadow (260)
(3, 13, 'HEALER', FALSE);   -- MalfurionDruid (258)

-- Raid 5: Cavernas del Tiempo 10N (min 140) — novatos y alts
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado) VALUES
(5, 3,  'HEALER', TRUE),    -- ThrallShaman (150) — puede entrar a esta
(5, 10, 'HEALER', TRUE),    -- KhadgarSabio (150) — puede entrar a esta
(5, 14, 'DPS',    TRUE),    -- RoninErrante (150) — sin clan pero puede jugar
(5, 15, 'DPS',    FALSE);   -- NoviceHero (150)

-- Raid 4: Templo Oscuro 10H (min 250) — solo los top, aun sin inscripciones
-- (queda libre para que los usuarios se inscriban desde el frontend)

-- ============================================================
-- 10. HISTORIAL DE BOTIN (items entregados en raids pasadas)
-- ============================================================
-- Registros directos en historial_botin (como si sp_repartir_botin ya se ejecuto).
-- No llamamos al SP aqui porque descontaria DKP de los valores ya definidos arriba.
INSERT INTO historial_botin (id_personaje, id_item, id_raid) VALUES
(1,  8,  3),    -- ArthasPvP recibio Coraza del Berserker en Naxx
(1,  1,  1),    -- ArthasPvP recibio Agonia de Escarcha en ICC
(2,  7,  3),    -- JainaFrost recibio Tunica de Telar en Naxx
(2,  4,  1),    -- JainaFrost recibio Baculo del Arcano en ICC
(4,  2,  1),    -- SylvanasArcher recibio Filo de Tormenta en ICC
(5,  15, 2),    -- UtherSanador recibio Pendiente del Oraculo en Ulduar
(7,  6,  3),    -- VoljinShadow recibio Pechera de Lordaeron en Naxx
(9,  1,  1),    -- MedivhArcano recibio Agonia de Escarcha en ICC
(11, 13, 3),    -- IllidanFuria recibio Amuleto de Fuego Solar en Naxx
(11, 8,  1),    -- IllidanFuria recibio Coraza del Berserker en ICC
(13, 6,  3);    -- MalfurionDruid recibio Pechera de Lordaeron en Naxx

-- ============================================================
-- 11. REFRESCAR VISTA MATERIALIZADA
-- ============================================================
SELECT refrescar_ranking_clan();

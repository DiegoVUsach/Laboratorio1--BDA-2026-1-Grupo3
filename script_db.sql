-- ============================================================
-- SCRIPT DE BASE DE DATOS - Grupo 3: Gestor de Clanes y Raids
-- Taller de Base de Datos - USACH 2026-1
-- ============================================================

-- Tabla Usuario
CREATE TABLE usuario (
                         id_usuario SERIAL PRIMARY KEY,
                         nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         rol VARCHAR(20) DEFAULT 'USER'
);

-- Tabla Clanes (sin FK de lider todavia, porque personaje aun no existe)
CREATE TABLE clanes (
                        id_clan SERIAL PRIMARY KEY,
                        nombre_clan VARCHAR(100) NOT NULL UNIQUE,
                        id_lider INTEGER
);

-- Tabla Personaje
CREATE TABLE personaje (
                           id_personaje SERIAL PRIMARY KEY,
                           id_usuario INTEGER NOT NULL,
                           id_clan INTEGER,
                           nombre_personaje VARCHAR(100) NOT NULL UNIQUE,
                           clase VARCHAR(50) NOT NULL,
                           faccion VARCHAR(50) NOT NULL,
                           rol_clan VARCHAR(50) DEFAULT 'Member',
                           nivel INTEGER DEFAULT 1,
                           item_level INTEGER DEFAULT 0,
                           puntos_dkp_actuales INTEGER DEFAULT 0,
                           CONSTRAINT chk_personaje_faccion
                               CHECK (faccion IN (
                                                  'Los Primordiales de la Luz',
                                                  'Los Hijos del Gris',
                                                  'Los Marcados por el Abismo'
                                   )),
                           CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                           CONSTRAINT fk_clan FOREIGN KEY (id_clan) REFERENCES clanes(id_clan) ON DELETE SET NULL
);

-- Resolver circularidad: ahora que personaje existe, agregamos la FK de lider
ALTER TABLE clanes
    ADD CONSTRAINT fk_lider
        FOREIGN KEY (id_lider) REFERENCES personaje(id_personaje) ON DELETE SET NULL;


-- ============================================================
-- Indice en clase del personaje (Req 8)
-- ============================================================
-- ¿Por que? Cuando el GM arma grupos para una raid, busca por clase
-- ("necesito 2 Paladines, 3 Magos"). Sin indice, PostgreSQL recorre
-- TODA la tabla personaje. Con el indice B-Tree, va directo.
CREATE INDEX idx_personaje_clase ON personaje(clase);

-- ============================================================
-- Tabla de Auditoria de Liderazgo
-- ============================================================
-- Aqui se registra automaticamente cada cambio de lider de un clan.
-- No la llena el backend, la llena el TRIGGER que crearemos mas abajo.
CREATE TABLE auditoria_liderazgo (
                                     id_auditoria SERIAL PRIMARY KEY,
                                     id_clan INTEGER,
                                     id_lider_anterior INTEGER,
                                     id_lider_nuevo INTEGER,
                                     fecha_transferencia TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Tabla de Raids
-- ============================================================
CREATE TABLE raids (
                       id_raid SERIAL PRIMARY KEY,
                       nombre_raid VARCHAR(100) NOT NULL,
                       fecha_raid TIMESTAMP NOT NULL,
                       item_level_minimo INTEGER NOT NULL,
                       tanques INTEGER NOT NULL,
                       healers INTEGER NOT NULL,
                       dps INTEGER NOT NULL,
                       estado VARCHAR(20) DEFAULT 'PROGRAMADA'
);

-- ============================================================
-- Tabla de Inscripciones a Raid
-- ============================================================
-- Relaciona un personaje con una raid. El campo confirmado
-- indica si el Guild Master ya aprobo la asistencia.
CREATE TABLE inscripciones_raid (
                                    id_inscripcion SERIAL PRIMARY KEY,
                                    id_raid INTEGER REFERENCES raids(id_raid),
                                    id_personaje INTEGER REFERENCES personaje(id_personaje),
                                    rol_en_raid VARCHAR(20),
                                    confirmado BOOLEAN DEFAULT FALSE,
                                    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    CONSTRAINT uq_inscripcion_unica UNIQUE (id_raid, id_personaje)
);

-- Indices adicionales (Req 8)
-- Aceleran las consultas de cupos por raid y el historial por personaje
CREATE INDEX idx_inscripciones_raid ON inscripciones_raid(id_raid);
CREATE INDEX idx_inscripciones_personaje ON inscripciones_raid(id_personaje);

-- ============================================================
-- Tabla de Items (Botin)
-- ============================================================
CREATE TABLE item (
                      id_item SERIAL PRIMARY KEY,
                      nombre_item VARCHAR(100) NOT NULL,
                      rareza VARCHAR(30) DEFAULT 'Comun',
                      tipo VARCHAR(20) DEFAULT 'ACCESORIO',
                      nivel INTEGER DEFAULT 1,
                      costo_dkp INTEGER NOT NULL,
                      CONSTRAINT chk_item_tipo
                          CHECK (tipo IN ('ARMADURA', 'ARMA', 'ACCESORIO'))
);

-- ============================================================
-- Tabla de Inventario
-- ============================================================
CREATE TABLE inventario (
                            id_inventario SERIAL PRIMARY KEY,
                            id_personaje INTEGER NOT NULL UNIQUE,
                            armadura_equipado INTEGER,
                            arma_equipado INTEGER,
                            accesorio_equipado INTEGER,
                            CONSTRAINT fk_inventario_personaje FOREIGN KEY (id_personaje)
                                REFERENCES personaje(id_personaje) ON DELETE CASCADE,
                            CONSTRAINT fk_inventario_armadura FOREIGN KEY (armadura_equipado)
                                REFERENCES item(id_item) ON DELETE SET NULL,
                            CONSTRAINT fk_inventario_arma FOREIGN KEY (arma_equipado)
                                REFERENCES item(id_item) ON DELETE SET NULL,
                            CONSTRAINT fk_inventario_accesorio FOREIGN KEY (accesorio_equipado)
                                REFERENCES item(id_item) ON DELETE SET NULL
);

-- ============================================================
-- Tabla de Items en Inventario
-- ============================================================
CREATE TABLE inventario_item (
                                 id_inventario INTEGER NOT NULL,
                                 id_item INTEGER NOT NULL,
                                 PRIMARY KEY (id_inventario, id_item),
                                 CONSTRAINT fk_inventario_item_inventario FOREIGN KEY (id_inventario)
                                     REFERENCES inventario(id_inventario) ON DELETE CASCADE,
                                 CONSTRAINT fk_inventario_item_item FOREIGN KEY (id_item)
                                     REFERENCES item(id_item) ON DELETE CASCADE
);

-- ============================================================
-- Tabla de Historial de Botin
-- ============================================================
-- Cada vez que se reparte un item, queda registrado aqui.
-- La llena el Stored Procedure sp_repartir_botin.
CREATE TABLE historial_botin (
                                 id_entrega SERIAL PRIMARY KEY,
                                 id_personaje INTEGER REFERENCES personaje(id_personaje),
                                 id_item INTEGER REFERENCES item(id_item),
                                 id_raid INTEGER REFERENCES raids(id_raid),
                                 fecha_entrega TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- ============================================================
-- REQUERIMIENTO 6: Trigger 2 - Auditar transferencia de liderazgo
-- ============================================================
-- ¿Que hace? Cada vez que alguien cambia el id_lider de un clan
-- (un UPDATE en la columna id_lider de la tabla clanes),
-- este trigger se dispara AUTOMATICAMENTE y guarda en auditoria_liderazgo
-- quien era el lider anterior y quien es el nuevo.
-- OLD = la fila ANTES del UPDATE, NEW = la fila DESPUES del UPDATE.

CREATE OR REPLACE FUNCTION funcion_auditar_liderazgo()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO auditoria_liderazgo (id_clan, id_lider_anterior, id_lider_nuevo)
VALUES (OLD.id_clan, OLD.id_lider, NEW.id_lider);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auditoria_liderazgo
    AFTER UPDATE OF id_lider ON clanes
    FOR EACH ROW
    EXECUTE FUNCTION funcion_auditar_liderazgo();

-- ============================================================
-- REQUERIMIENTO 5: Trigger 1 - Validar item level en inscripcion
-- ============================================================
-- ¿Que hace? Cuando un personaje intenta inscribirse a una raid
-- (INSERT en inscripciones_raid), este trigger verifica que el
-- item_level del personaje sea >= al item_level_minimo de la raid.
-- Si no cumple, lanza una EXCEPTION y el INSERT se cancela.

CREATE OR REPLACE FUNCTION funcion_validar_item_level()
RETURNS TRIGGER AS $$
DECLARE
lvl_requerido INTEGER;
    lvl_personaje INTEGER;
BEGIN
SELECT item_level_minimo INTO lvl_requerido FROM raids WHERE id_raid = NEW.id_raid;
SELECT item_level INTO lvl_personaje FROM personaje WHERE id_personaje = NEW.id_personaje;

IF lvl_personaje < lvl_requerido THEN
        RAISE EXCEPTION 'Tu nivel de equipo (%) es insuficiente para esta Raid (Minimo: %)',
        lvl_personaje, lvl_requerido;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_item_level
    BEFORE INSERT ON inscripciones_raid
    FOR EACH ROW
    EXECUTE FUNCTION funcion_validar_item_level();

-- ============================================================
-- REQUERIMIENTO 3: SP1 - Distribuir botin (deducir DKP)
-- ============================================================
-- ¿Que hace? En una sola transaccion atomica:
-- 1. Verifica que el item existe
-- 2. Verifica que el personaje tiene suficientes DKP
-- 3. Descuenta los DKP del personaje
-- 4. Registra la entrega en historial_botin
-- Si algo falla, todo se revierte (atomicidad).

CREATE OR REPLACE PROCEDURE sp_repartir_botin(
    p_id_personaje INTEGER,
    p_id_item INTEGER,
    p_id_raid INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
v_costo INTEGER;
    v_dkp_actual INTEGER;
BEGIN
SELECT costo_dkp INTO v_costo FROM item WHERE id_item = p_id_item;
IF v_costo IS NULL THEN
        RAISE EXCEPTION 'El item con ID % no existe', p_id_item;
END IF;

SELECT puntos_dkp_actuales INTO v_dkp_actual FROM personaje WHERE id_personaje = p_id_personaje;
IF v_dkp_actual IS NULL THEN
        RAISE EXCEPTION 'El personaje con ID % no existe', p_id_personaje;
END IF;

    IF v_dkp_actual < v_costo THEN
        RAISE EXCEPTION 'DKP insuficientes (Tiene: %, Necesita: %)', v_dkp_actual, v_costo;
END IF;

UPDATE personaje
SET puntos_dkp_actuales = puntos_dkp_actuales - v_costo
WHERE id_personaje = p_id_personaje;

INSERT INTO historial_botin (id_personaje, id_item, id_raid)
VALUES (p_id_personaje, p_id_item, p_id_raid);
END;
$$;

-- ============================================================
-- REQUERIMIENTO 4: SP2 - Invitacion masiva de raiders
-- ============================================================
-- ¿Que hace? Recibe un id_raid y un id_clan, y automaticamente
-- inscribe a TODOS los personajes del clan que tengan rol 'Raider'.
-- El Trigger 1 se activa en cada INSERT, asi que si un Raider
-- no tiene suficiente item_level, ESE personaje falla pero los
-- demas se inscriben igual.

CREATE OR REPLACE PROCEDURE sp_invitacion_masiva_raiders(
    p_id_raid INTEGER,
    p_id_clan INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
r RECORD;
BEGIN
-- Recorremos cada Raider del clan individualmente.
-- Si uno falla el Trigger de item_level, solo ESE se salta;
-- los demas se inscriben normalmente.
FOR r IN
SELECT id_personaje
FROM personaje
WHERE id_clan = p_id_clan
  AND rol_clan = 'Raider'
    LOOP
BEGIN
INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado)
VALUES (p_id_raid, r.id_personaje, 'DPS', FALSE);
EXCEPTION WHEN OTHERS THEN
        -- Este raider no cumple el item_level minimo (u otra restriccion).
        -- Lo saltamos y continuamos con el siguiente.
        RAISE NOTICE 'No se pudo inscribir personaje %: %', r.id_personaje, SQLERRM;
END;
END LOOP;
END;
$$;

-- ============================================================
-- REQUERIMIENTO 7: Vista Materializada - Ranking del clan
-- ============================================================
-- ¿Que es una vista materializada? Es como una tabla que se genera
-- a partir de un SELECT complejo. A diferencia de una vista normal,
-- los datos quedan GUARDADOS en disco, asi que las lecturas son
-- instantaneas. La desventaja es que hay que refrescarla manualmente.

CREATE MATERIALIZED VIEW vista_ranking_clan AS
SELECT
    p.id_personaje,
    p.nombre_personaje,
    c.nombre_clan,
    COUNT(i.id_inscripcion) FILTER (WHERE i.confirmado = TRUE) AS raids_asistidas,
    p.puntos_dkp_actuales
FROM personaje p
         JOIN clanes c ON p.id_clan = c.id_clan
         LEFT JOIN inscripciones_raid i ON p.id_personaje = i.id_personaje
GROUP BY p.id_personaje, p.nombre_personaje, c.nombre_clan, p.puntos_dkp_actuales
ORDER BY raids_asistidas DESC, p.puntos_dkp_actuales DESC;

-- Funcion para refrescar la vista (el enunciado pide logica de refresco)
CREATE OR REPLACE FUNCTION refrescar_ranking_clan()
RETURNS VOID AS $$
BEGIN
    REFRESH MATERIALIZED VIEW vista_ranking_clan;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Trigger adicional: Recalcular item_level al cambiar equipo
-- ============================================================
-- ¿Que hace? Cuando un personaje equipa o desequipa un item
-- (UPDATE en la tabla inventario), este trigger recalcula
-- automaticamente el item_level del personaje como el promedio
-- del nivel de los items equipados en sus 3 slots.
-- Si no tiene nada equipado, el item_level queda en 0.
-- Esto es coherente con la mecanica de un MMORPG: tu poder
-- de equipo depende de lo que llevas puesto, no de un numero manual.

CREATE OR REPLACE FUNCTION funcion_recalcular_item_level()
RETURNS TRIGGER AS $$
DECLARE
v_id_personaje INTEGER;
    v_nuevo_item_level INTEGER;
BEGIN
    v_id_personaje := NEW.id_personaje;

SELECT COALESCE(
               AVG(i.nivel)::INTEGER, 0
       ) INTO v_nuevo_item_level
FROM (
         SELECT armadura_equipado AS id_item FROM inventario WHERE id_personaje = v_id_personaje AND armadura_equipado IS NOT NULL
         UNION ALL
         SELECT arma_equipado FROM inventario WHERE id_personaje = v_id_personaje AND arma_equipado IS NOT NULL
         UNION ALL
         SELECT accesorio_equipado FROM inventario WHERE id_personaje = v_id_personaje AND accesorio_equipado IS NOT NULL
     ) slots
         JOIN item i ON slots.id_item = i.id_item;

UPDATE personaje
SET item_level = v_nuevo_item_level
WHERE id_personaje = v_id_personaje;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_recalcular_item_level
    AFTER INSERT OR UPDATE ON inventario
                        FOR EACH ROW
                        EXECUTE FUNCTION funcion_recalcular_item_level();


-- Nuevo Trigger incoherencia faccion clan
CREATE OR REPLACE FUNCTION funcion_validar_faccion_clan()
RETURNS TRIGGER AS $$
DECLARE
    faccion_clan VARCHAR(50);
BEGIN
    -- Buscamos la facción de cualquier miembro que ya esté en ese clan
    SELECT faccion INTO faccion_clan 
    FROM personaje 
    WHERE id_clan = NEW.id_clan 
    LIMIT 1;

    -- Si el clan no está vacío y la facción del nuevo es distinta, lanzamos error
    IF faccion_clan IS NOT NULL AND faccion_clan <> NEW.faccion THEN
        RAISE EXCEPTION 'Incoherencia de facción: El clan ya pertenece a % y el personaje es %', 
        faccion_clan, NEW.faccion;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_faccion_clan
    BEFORE INSERT OR UPDATE OF id_clan ON personaje
    FOR EACH ROW
    EXECUTE FUNCTION funcion_validar_faccion_clan();

-- ============================================================

-- Nuevo Trigger Item level no relacionado a item equipado
CREATE OR REPLACE FUNCTION funcion_bloquear_item_level_manual()
RETURNS TRIGGER AS $$
BEGIN
    -- Si alguien intenta cambiar el item_level directamente en la tabla personaje...
    -- lo devolvemos a su valor original (OLD) a menos que el cambio venga del trigger del inventario.
    -- O mejor aún: lanzamos un error indicando que debe equipar items.
    
    IF (TG_OP = 'UPDATE') AND (NEW.item_level <> OLD.item_level) THEN
        -- Aquí podrías permitir el cambio solo si es una "llamada interna"
        -- Pero lo más sano es que el item_level sea una columna de solo lectura para el usuario.
        RAISE NOTICE 'El Item Level solo puede ser modificado equipando objetos en el inventario.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================

-- Nuevo Trigger para evitar que un personaje sin clan tenga rol de Raider o Guild Master
CREATE OR REPLACE FUNCTION funcion_validar_rol_sin_clan()
RETURNS TRIGGER AS $$
BEGIN
    IF (NEW.id_clan IS NULL) AND (NEW.rol_clan IN ('
Raider', 'Guild Master')) THEN
        RAISE EXCEPTION 'Un personaje sin clan no puede tener rol de Raider o Guild Master.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
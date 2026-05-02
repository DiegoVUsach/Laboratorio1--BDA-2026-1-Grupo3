-- Crear Usuario
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Crear Clanes (Sin la FK de líder todavía)
CREATE TABLE clanes (
    id_clan SERIAL PRIMARY KEY,
    nombre_clan VARCHAR(100) NOT NULL UNIQUE,
    id_lider INTEGER -- Aun no es FK
);

-- Crear Personaje (Ahora puede referenciar a usuario y a clanes)
CREATE TABLE personaje (
    id_personaje SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    id_clan INTEGER, 
    nombre_personaje VARCHAR(100) NOT NULL UNIQUE,
    clase VARCHAR(50) NOT NULL, 
    rol_clan VARCHAR(50) DEFAULT 'Member', 
    nivel INTEGER DEFAULT 1,
    item_level INTEGER DEFAULT 0,
    puntos_dkp_actuales INTEGER DEFAULT 0,
    
    CONSTRAINT fk_usuario FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_clan FOREIGN KEY(id_clan) REFERENCES clanes(id_clan) ON DELETE SET NULL
);

-- Ahora añadimos la FK de Liderazgo a Clanes (Resolviendo la circularidad)
ALTER TABLE clanes 
ADD CONSTRAINT fk_lider 
FOREIGN KEY (id_lider) REFERENCES personaje(id_personaje) ON DELETE SET NULL;

-- Crear indice (Requerimiento 8)
CREATE INDEX idx_personaje_clase ON personaje(clase);

-- Tabla de Auditoría
CREATE TABLE auditoria_liderazgo (
    id_auditoria SERIAL PRIMARY KEY,
    id_clan INTEGER,
    id_lider_anterior INTEGER,
    id_lider_nuevo INTEGER,
    fecha_transferencia TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Función y Trigger (Requerimiento 6)
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

CREATE TABLE inscripciones_raid (
    id_inscripcion SERIAL PRIMARY KEY,
    id_raid INTEGER REFERENCES raids(id_raid),
    id_personaje INTEGER REFERENCES personaje(id_personaje),
    rol_en_raid VARCHAR(20), -- 'TANQUE', 'HEALER', 'DPS'
    confirmado BOOLEAN DEFAULT FALSE,
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE OR REPLACE FUNCTION funcion_validar_item_level()
RETURNS TRIGGER AS $$
DECLARE
    lvl_requerido INTEGER;
    lvl_personaje INTEGER;
BEGIN
    -- Obtener nivel requerido de la raid
    SELECT item_level_minimo INTO lvl_requerido FROM raids WHERE id_raid = NEW.id_raid;
    -- Obtener nivel del personaje
    SELECT item_level INTO lvl_personaje FROM personaje WHERE id_personaje = NEW.id_personaje;

    IF lvl_personaje < lvl_requerido THEN
        RAISE EXCEPTION 'Tu nivel de equipo (%) es insuficiente para esta Raid (Mínimo: %)', 
        lvl_personaje, lvl_requerido;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_item_level
BEFORE INSERT ON inscripciones_raid
FOR EACH ROW
EXECUTE FUNCTION funcion_validar_item_level();


CREATE OR REPLACE PROCEDURE sp_invitacion_masiva_raiders(
    p_id_raid INTEGER,
    p_id_clan INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Insertamos en la tabla de inscripciones a todos los que cumplan el criterio
    INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid, confirmado)
    SELECT 
        p_id_raid, 
        id_personaje, 
        'DPS', -- Por defecto los invitamos como DPS, el GM puede cambiarlos luego
        FALSE  -- No confirmados hasta que el GM o el usuario acepte
    FROM personaje
    WHERE id_clan = p_id_clan 
      AND rol_clan = 'Raider';

    -- NOTA: El TRIGGER 1 (Item Level) se activará automáticamente aquí.
    -- Si un Raider no tiene el nivel suficiente, la inserción de ESE personaje fallará.
END;
$$;

-- Tabla de Items (Botín disponible)
CREATE TABLE item (
    id_item SERIAL PRIMARY KEY,
    nombre_item VARCHAR(100) NOT NULL,
    costo_dkp INTEGER NOT NULL -- Cuánto cuesta en puntos
);

-- Tabla para saber quién se llevó qué (Historial de Botín)
CREATE TABLE historial_botin (
    id_entrega SERIAL PRIMARY KEY,
    id_personaje INTEGER REFERENCES personaje(id_personaje),
    id_item INTEGER REFERENCES item(id_item),
    id_raid INTEGER REFERENCES raids(id_raid),
    fecha_entrega TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- REQUERIMIENTO: Procedimiento Almacenado 1 (Distribuir Botín)
-- Descuenta DKP al personaje y registra la entrega en una sola transacción
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
    -- Obtener costo del item
    SELECT costo_dkp INTO v_costo FROM item WHERE id_item = p_id_item;
    
    -- Obtener DKP del personaje
    SELECT puntos_dkp_actuales INTO v_dkp_actual FROM personaje WHERE id_personaje = p_id_personaje;

    -- Validar si tiene puntos suficientes
    IF v_dkp_actual < v_costo THEN
        RAISE EXCEPTION 'El personaje no tiene suficientes DKP (Tiene: %, Necesita: %)', v_dkp_actual, v_costo;
    END IF;

    --  Descontar puntos
    UPDATE personaje 
    SET puntos_dkp_actuales = puntos_dkp_actuales - v_costo 
    WHERE id_personaje = p_id_personaje;

    -- Registrar en historial
    INSERT INTO historial_botin (id_personaje, id_item, id_raid) 
    VALUES (p_id_personaje, p_id_item, p_id_raid);
END;
$$;

-- REQUERIMIENTO: Vista Materializada (Ranking de Contribución)
-- Se pide una vista que analice la participación
CREATE MATERIALIZED VIEW vista_ranking_clan AS
SELECT 
    p.nombre_personaje,
    c.nombre_clan,
    COUNT(i.id_inscripcion) FILTER (WHERE i.confirmado = TRUE) as raids_asistidas,
    p.puntos_dkp_actuales
FROM personaje p
JOIN clanes c ON p.id_clan = c.id_clan
LEFT JOIN inscripciones_raid i ON p.id_personaje = i.id_personaje
GROUP BY p.id_personaje, p.nombre_personaje, c.nombre_clan, p.puntos_dkp_actuales
ORDER BY p.puntos_dkp_actuales DESC;
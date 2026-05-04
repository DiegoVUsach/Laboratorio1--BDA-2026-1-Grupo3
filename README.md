# Gestor de Clanes y Raids para MMORPG

**Taller de Base de Datos Diurno 1-2026 — Universidad de Santiago de Chile**
**Grupo 3**

## Arquitectura y Tecnologías

| Capa | Tecnología |
|---|---|
| Base de Datos | PostgreSQL 16 |
| Backend (API REST) | Java 21, Spring Boot 3, JdbcTemplate (sin ORM) |
| Frontend | Vue 3 (Composition API), Vite, TypeScript |
| Seguridad | JWT (JSON Web Tokens), BCrypt, HTTPS (keystore PKCS12) |
| Despliegue | Docker Compose (3 contenedores: PostgreSQL, Spring Boot, Nginx) |

## Manual de Instalación

### Prerrequisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecución.
- Puerto **80** libre (frontend), puerto **8443** libre (backend), puerto **5555** libre (PostgreSQL).
- Conexión a internet (para descargar imágenes Docker la primera vez).

### Pasos

```bash
# 1. Clonar el repositorio
git clone <URL_DEL_REPOSITORIO>
cd Laboratorio1--BDA-2026-1-Grupo3

# 2. Levantar todos los servicios (BD + Backend + Frontend)
docker-compose up --build

# 3. Esperar a que aparezcan estos mensajes:
#    lab1-postgres  | database system is ready to accept connections
#    lab1-app       | Started Laboratorio1Application
#    lab1-frontend  | start worker process

# 4. Abrir en el navegador:
#    http://localhost
```

### Credenciales de prueba

Todos los usuarios tienen contraseña: `123456`

| Usuario | Rol | Descripción |
|---|---|---|
| `admin` | ADMIN | Administrador del sistema |
| `jugador1` | USER | Tiene personajes en Clan 1 (JainaFrost, ThrallShaman) |
| `jugador2` | USER | Tiene SylvanasArcher en Clan 1 |
| `jugador3` | USER | Tiene VoljinShadow (GM Clan 2) y GarroshWarrior |
| `jugador4` - `jugador7` | USER | Usuarios adicionales con personajes en distintos clanes |

### Detener el sistema

```bash
docker-compose down       # Detener (conserva datos)
docker-compose down -v    # Detener y borrar datos (reinstalación limpia)
```

### Solución de problemas

| Problema | Solución |
|---|---|
| Puerto 80 ocupado | Cambiar `"80:80"` en docker-compose.yml |
| El backend no conecta a la BD | Verificar que `lab1-postgres` diga "ready to accept connections" antes de que arranque `lab1-app` |
| Error de SSL/certificado | El backend usa HTTPS internamente; nginx hace proxy automático. No acceder a `https://localhost:8443` directamente |
| Datos corruptos | Ejecutar `docker-compose down -v && docker-compose up --build` |

## Documentación de la API

### Autenticación

Todos los endpoints (excepto login y register) requieren el header:
```
Authorization: Bearer <token_jwt>
```

**POST /api/auth/login**
```json
{ "username": "jugador1", "password": "123456" }
```
Respuesta: `{ "token": "eyJhbGciOiJIUzI1NiJ9..." }`

**POST /api/auth/register**
```json
{ "nombreUsuario": "nuevo_jugador", "password": "mi_password" }
```

### Personajes

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/personajes?page=0&size=10` | Listar personajes (paginado) |
| GET | `/api/personajes/{id}` | Obtener personaje por ID |
| GET | `/api/personajes/mis-personajes` | Personajes del usuario autenticado |
| POST | `/api/personajes` | Crear personaje |
| PUT | `/api/personajes/{id}` | Actualizar personaje (solo dueño) |
| DELETE | `/api/personajes/{id}` | Eliminar personaje (solo dueño) |
| PUT | `/api/personajes/asignar-rol` | Cambiar rol de miembro (solo GM) |

Ejemplo de creación:
```json
{
  "nombrePersonaje": "NuevoHeroe",
  "clase": "Mago",
  "faccion": "Los Hijos del Gris"
}
```

### Clanes

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/clanes` | Listar clanes |
| GET | `/api/clanes/{id}` | Obtener clan por ID |
| POST | `/api/clanes` | Crear clan |
| PUT | `/api/clanes/{id}` | Actualizar clan |
| PUT | `/api/clanes/{id}/transfer-leadership` | Transferir liderazgo (dispara trigger de auditoría) |
| DELETE | `/api/clanes/{id}` | Eliminar clan (solo líder) |

### Raids

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/raids/calendario` | Calendario semanal con cupos por rol |
| POST | `/api/raids` | Crear raid (solo GM, invita raiders automáticamente) |
| POST | `/api/raids/inscribirse` | Inscribirse a raid (trigger valida item level) |
| POST | `/api/raids/invitar-raiders` | Invitación masiva vía SP2 (solo GM) |
| DELETE | `/api/raids/desinscribirse` | Anular inscripción |

### Items e Inventario

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/items?page=0&size=100` | Listar items |
| POST | `/api/items/repartir` | Repartir botín vía SP1 (descuenta DKP) |
| GET | `/api/items/historial/{idPersonaje}` | Historial de botín de un personaje |
| GET | `/api/items/ranking` | Ranking desde vista materializada |
| POST | `/api/items/ranking/refrescar` | Refrescar vista materializada |
| GET | `/api/inventarios/por-personaje/{id}` | Inventario con equipo actual |
| GET | `/api/inventarios/por-personaje/{id}/items` | Items en bolsa del personaje |
| PUT | `/api/inventarios/id/{id}` | Equipar/desequipar items (dispara trigger de recálculo) |

## Componentes de Base de Datos

### Triggers

| Trigger | Tabla | Evento | Función |
|---|---|---|---|
| `trg_auditoria_liderazgo` | clanes | AFTER UPDATE id_lider | Registra cambios de liderazgo en `auditoria_liderazgo` |
| `trg_validar_item_level` | inscripciones_raid | BEFORE INSERT | Rechaza inscripción si item_level < mínimo de la raid |
| `trg_recalcular_item_level` | inventario | AFTER INSERT/UPDATE | Recalcula item_level del personaje desde equipo |
| `trg_validar_faccion_clan` | personaje | BEFORE INSERT/UPDATE id_clan | Impide mezclar facciones en un mismo clan |

### Stored Procedures

| Procedimiento | Descripción |
|---|---|
| `sp_repartir_botin(id_personaje, id_item, id_raid)` | Transacción atómica: verifica DKP, descuenta y registra en historial |
| `sp_invitacion_masiva_raiders(id_raid, id_clan)` | Inscribe todos los Raiders del clan con manejo individual de errores |

### Vista Materializada

`vista_ranking_clan`: Ranking de personajes por raids asistidas y DKP, refrescable con `SELECT refrescar_ranking_clan()`.

### Índices

| Índice | Columna | Justificación |
|---|---|---|
| `idx_personaje_clase` | personaje(clase) | Búsqueda por clase al armar grupos |
| `idx_inscripciones_raid` | inscripciones_raid(id_raid) | Cálculo de cupos por raid |
| `idx_inscripciones_personaje` | inscripciones_raid(id_personaje) | Historial de inscripciones por personaje |

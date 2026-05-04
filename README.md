# Gestor de Clanes y Raids para MMORPG (Grupo 3)
Base de Datos Avanzada, 1-2026. Universidad de Santiago de Chile.

## Tecnologías usadas
- PostgreSQL 16
- Backend: Java 21, Spring Boot 3, JdbcTemplate (sin ORM)
- Frontend: Vue 3, Vite, TypeScript
- Seguridad: JWT, BCrypt, HTTPS interno
- Despliegue: Docker Compose

## Cómo levantar el proyecto

**Requisitos previos:**
- Tener Docker y **Docker Compose V2** instalados.
    - **Windows / Mac:** Basta con tener Docker Desktop instalado.
    - **Linux:** Instalar Docker y el plugin de Compose (puedes revisar la guía oficial en https://docs.docker.com/engine/install).

*(Nota: Si al ejecutar los pasos tu terminal no reconoce el comando `docker compose`, significa que tienes una versión antigua. En ese caso, simplemente usa el comando con guion: `docker-compose`).*

**Pasos:**
1. Clonar repositorio y dirigirse a la carpeta:
```bash
git clone https://github.com/DiegoVUsach/Laboratorio1--BDA-2026-1-Grupo3
cd Laboratorio1--BDA-2026-1-Grupo3
```
Una vez en la raiz del proyecto, procedemos a:
2. Levanta los servicios:
```bash
docker compose up --build
```

3. Espera a que la terminal muestre que la base de datos está lista (`database system is ready to accept connections`) y que el backend de Spring Boot haya arrancado (aparecerá el logo por consola).

4. Abre tu navegador y entra a: `http://localhost`

Para detener el sistema normalmente, usa `docker compose down`.

Una vez en la plataforma:
## Usuarios de prueba
Todos los usuarios tienen la contraseña: `123456`
- **admin**: Rol de administrador.
- **jugador1** a **jugador7**: Tienen el rol de usuario normal. Cada uno ya viene con personajes creados en distintos clanes para poder probar las funciones.

## Solución de problemas

- **Error "port is already allocated" o similar en la terminal:**
  Esto pasa si un puerto ya está en uso en tu PC. Abre el archivo `docker-compose.yml` y busca el servicio que falló. Cambia el primer número de la línea de puertos (ejemplo: cambia `"80:80"` a `"8080:80"`, o `"5555:5432"` a `"5556:5432"`). Luego vuelve a levantar con `docker compose up --build`.

- **El sistema se quedó pegado, hay datos corruptos o conflictos con contenedores viejos:**
  Abre la terminal en la carpeta del proyecto y ejecuta una limpieza profunda con:
  `docker compose down -v --remove-orphans`
  Después de eso, vuelve a levantar el proyecto normalmente.

- **Error de SSL o certificado en el navegador:**
  El backend usa HTTPS internamente, pero el contenedor de Nginx hace el proxy automáticamente. No intentes acceder a `https://localhost` o al puerto 8443 directamente, usa siempre `http://localhost`.

## Resumen de la API
Para usar los endpoints (excepto login y registro), necesitas mandar un token JWT en el header de tu petición: `Authorization: Bearer <token>`.

- **/api/auth**: Login y registro.
- **/api/personajes**: Crear, editar, listar personajes y cambiar roles dentro de un clan.
- **/api/clanes**: Crear y administrar clanes. Al transferir el liderazgo, se dispara un trigger de auditoría en la BD.
- **/api/raids**: Ver el calendario semanal, crear raids e inscribirse. El líder puede usar un endpoint de invitación masiva que corre por un Procedimiento Almacenado.
- **/api/items e /inventarios**: Repartir botín (descuenta DKP), ver historiales y equipar items (esto dispara un trigger que recalcula tu Item Level).

## Detalles de la Base de Datos
El proyecto incluye lógica directamente en PostgreSQL:
- **Triggers:** Auditan los cambios de liderazgo de los clanes, validan el Item Level antes de dejarte entrar a una raid, impiden mezclar facciones en un mismo clan y recalculan las estadísticas al equipar un item.
- **Procedimientos Almacenados:** Hay un SP para el reparto de botín (transacción atómica que revisa y descuenta DKP) y otro para hacer inscripciones masivas a las raids manejando errores individuales.
- **Vista Materializada:** Existe una vista `vista_ranking_clan` que arma el ranking de jugadores por asistencia y DKP. Tiene su propio endpoint para refrescarse.
- **Índices:** Creados estratégicamente en columnas muy consultadas, como la clase del personaje y el ID de las raids en las inscripciones, para agilizar el armado de grupos.
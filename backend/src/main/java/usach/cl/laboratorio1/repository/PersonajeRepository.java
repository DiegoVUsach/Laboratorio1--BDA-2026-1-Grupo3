package usach.cl.laboratorio1.repository;

import usach.cl.laboratorio1.tablas.Personaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonajeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Personaje> rowMapper = (rs, rowNum) -> {
        Personaje p = new Personaje();
        p.setIdPersonaje(rs.getInt("id_personaje"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setIdClan((Integer) rs.getObject("id_clan"));
        p.setNombrePersonaje(rs.getString("nombre_personaje"));
        p.setClase(rs.getString("clase"));
        p.setFaccion(Personaje.Faccion.fromLabel(rs.getString("faccion")));
        p.setRolClan(rs.getString("rol_clan"));
        p.setItemLevel(rs.getInt("item_level"));
        p.setPuntosDkpActuales(rs.getInt("puntos_dkp_actuales"));
        return p;
    };

    // Paginacion: LIMIT controla cuantos resultados, OFFSET salta los anteriores.
    // Ejemplo: page=0, size=10 → LIMIT 10 OFFSET 0 (primeros 10)
    //          page=1, size=10 → LIMIT 10 OFFSET 10 (siguientes 10)
    public List<Personaje> findAll(int page, int size) {
        String sql = "SELECT * FROM personaje ORDER BY id_personaje LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, rowMapper, size, page * size);
    }

    // Mantener version sin paginacion para uso interno
    public List<Personaje> findAll() {
        return jdbcTemplate.query("SELECT * FROM personaje", rowMapper);
    }

    public Personaje findById(Integer id) {
        List<Personaje> list = jdbcTemplate.query(
                "SELECT * FROM personaje WHERE id_personaje = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // Buscar todos los personajes de un usuario por su username.
    // Hace un JOIN con la tabla usuario para filtrar por nombre.
    // Se usa en el endpoint "mis-personajes".
    public List<Personaje> findByUsuario(String username) {
        String sql = "SELECT p.* FROM personaje p " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "WHERE u.nombre_usuario = ?";
        return jdbcTemplate.query(sql, rowMapper, username);
    }

    public int save(Personaje p) {
        String sql = "INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, " +
            "clase, faccion, rol_clan, item_level, puntos_dkp_actuales) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                p.getIdUsuario(), p.getIdClan(), p.getNombrePersonaje(),
                p.getClase(), p.getFaccion().getLabel(),
                p.getRolClan() != null ? p.getRolClan() : "Member",
                p.getItemLevel() != null ? p.getItemLevel() : 0,
                p.getPuntosDkpActuales() != null ? p.getPuntosDkpActuales() : 0);
    }

    public int update(Personaje p) {
        String sql = "UPDATE personaje SET nombre_personaje = ?, clase = ?, " +
            "faccion = ?, item_level = ? WHERE id_personaje = ?";
        return jdbcTemplate.update(sql,
            p.getNombrePersonaje(), p.getClase(), p.getFaccion().getLabel(),
            p.getItemLevel(), p.getIdPersonaje());
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update(
                "DELETE FROM personaje WHERE id_personaje = ?", id);
    }

    // Cambiar el rol de un personaje dentro del clan.
    // Se usa cuando el Guild Master asigna roles (Raider, Member, etc.)
    public int updateRol(Integer idPersonaje, String nuevoRol) {
        return jdbcTemplate.update(
                "UPDATE personaje SET rol_clan = ? WHERE id_personaje = ?",
                nuevoRol, idPersonaje);
    }

    // Verifica si un personaje es el lider de un clan.
    // Se usa para validar que solo el Guild Master pueda
    // hacer ciertas acciones (asignar roles, transferir liderazgo).
    public boolean esLiderDeClan(Integer idPersonaje, Integer idClan) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM clanes WHERE id_clan = ? AND id_lider = ?",
                Integer.class, idClan, idPersonaje);
        return count != null && count > 0;
    }

    // Verifica si un personaje pertenece al usuario autenticado.
    // Esto es CLAVE para la seguridad JWT (Req 2):
    // un usuario solo puede operar con SUS propios personajes.
    // Hace un JOIN entre personaje y usuario para cruzar el id
    // del personaje con el nombre del usuario del token JWT.
    public boolean perteneceAUsuario(Integer idPersonaje, String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM personaje p " +
                        "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                        "WHERE p.id_personaje = ? AND u.nombre_usuario = ?",
                Integer.class, idPersonaje, username);
        return count != null && count > 0;
    }
}
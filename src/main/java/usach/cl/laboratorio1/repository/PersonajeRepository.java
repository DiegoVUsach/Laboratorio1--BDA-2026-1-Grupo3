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
        p.setRolClan(rs.getString("rol_clan"));
        p.setNivel(rs.getInt("nivel"));
        p.setItemLevel(rs.getInt("item_level"));
        p.setPuntosDkpActuales(rs.getInt("puntos_dkp_actuales"));
        return p;
    };

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
                "clase, rol_clan, nivel, item_level, puntos_dkp_actuales) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                p.getIdUsuario(), p.getIdClan(), p.getNombrePersonaje(),
                p.getClase(),
                p.getRolClan() != null ? p.getRolClan() : "Member",
                p.getNivel() != null ? p.getNivel() : 1,
                p.getItemLevel() != null ? p.getItemLevel() : 0,
                p.getPuntosDkpActuales() != null ? p.getPuntosDkpActuales() : 0);
    }

    public int update(Personaje p) {
        String sql = "UPDATE personaje SET nombre_personaje = ?, clase = ?, " +
                "nivel = ?, item_level = ? WHERE id_personaje = ?";
        return jdbcTemplate.update(sql,
                p.getNombrePersonaje(), p.getClase(),
                p.getNivel(), p.getItemLevel(), p.getIdPersonaje());
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
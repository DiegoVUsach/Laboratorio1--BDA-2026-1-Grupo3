package usach.cl.laboratorio1.repository;

import usach.cl.laboratorio1.tablas.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository le dice a Spring que esta clase maneja acceso a datos.
// Spring la detecta automaticamente y la hace disponible para inyeccion.
@Repository
public class UsuarioRepository {

    // JdbcTemplate es la herramienta de Spring para ejecutar SQL puro.
    // NO es un ORM: nosotros escribimos el SQL a mano.
    // @Autowired le dice a Spring "inyectame una instancia de JdbcTemplate".
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper: le enseña a JdbcTemplate como convertir una fila SQL
    // en un objeto Java. Por cada fila del ResultSet, crea un Usuario
    // y mapea cada columna al campo correspondiente.
    private final RowMapper<Usuario> rowMapper = (rs, rowNum) -> {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));
        return u;
    };

    // SELECT * FROM usuario → devuelve lista de todos los usuarios
    public List<Usuario> findAll() {
        return jdbcTemplate.query("SELECT * FROM usuario", rowMapper);
    }

    // Buscar por ID. Retorna null si no existe.
    public Usuario findById(Integer id) {
        List<Usuario> list = jdbcTemplate.query(
                "SELECT * FROM usuario WHERE id_usuario = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // Buscar por nombre de usuario (para el login).
    public Usuario findByUsername(String username) {
        List<Usuario> list = jdbcTemplate.query(
                "SELECT * FROM usuario WHERE nombre_usuario = ?", rowMapper, username);
        return list.isEmpty() ? null : list.get(0);
    }

    // INSERT: el ? se reemplaza por los parametros en orden.
    // Esto previene SQL injection.
    public int save(Usuario entity) {
        String sql = "INSERT INTO usuario (nombre_usuario, password, rol) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql,
                entity.getNombreUsuario(),
                entity.getPassword(),
                entity.getRol() != null ? entity.getRol() : "USER");
    }

    // UPDATE: modifica un usuario existente por su ID.
    public int update(Usuario entity) {
        String sql = "UPDATE usuario SET nombre_usuario = ?, password = ? WHERE id_usuario = ?";
        return jdbcTemplate.update(sql,
                entity.getNombreUsuario(),
                entity.getPassword(),
                entity.getIdUsuario());
    }

    // DELETE: elimina un usuario por su ID.
    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM usuario WHERE id_usuario = ?", id);
    }
}
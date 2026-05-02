package com.example.demo.repository;
import com.example.demo.tablas.Personaje;
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
        p.setIdClan(rs.getInt("id_clan"));
        p.setNombrePersonaje(rs.getString("nombre_personaje"));
        p.setClase(rs.getString("clase"));
        p.setRolClan(rs.getString("rol_clan"));
        p.setNivel(rs.getInt("nivel"));
        p.setItemLevel(rs.getInt("item_level"));
        p.setPuntosDkpActuales(rs.getInt("puntos_dkp_actuales"));
        return p;
    };

    public int save(Personaje p) {
        String sql = "INSERT INTO personaje (id_usuario, id_clan, nombre_personaje, clase, rol_clan, nivel, item_level, puntos_dkp_actuales) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, p.getIdUsuario(), p.getIdClan(), p.getNombrePersonaje(), 
                                   p.getClase(), p.getRolClan(), p.getNivel(), p.getItemLevel(), 0);
    }

    public List<Personaje> findAll() {
        return jdbcTemplate.query("SELECT * FROM personaje", rowMapper);
    }

    public Personaje findById(Integer id) {
        String sql = "SELECT * FROM personaje WHERE id_personaje = ?";
        List<Personaje> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int updateRol(Integer idPersonaje, String nuevoRol) {
        return jdbcTemplate.update("UPDATE personaje SET rol_clan = ? WHERE id_personaje = ?", nuevoRol, idPersonaje);
    }

    
    public boolean esLiderDeClan(Integer idPersonaje, Integer idClan) {
        String sql = "SELECT COUNT(*) FROM clanes WHERE id_clan = ? AND id_lider = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idClan, idPersonaje);
        return count != null && count > 0;
    }

    public boolean perteneceAUsuario(Integer idPersonaje, String username) {
        String sql = "SELECT COUNT(*) FROM personaje p JOIN usuario u ON p.id_usuario = u.id_usuario " +
                     "WHERE p.id_personaje = ? AND u.nombre_usuario = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idPersonaje, username);
        return count != null && count > 0;
    }
}
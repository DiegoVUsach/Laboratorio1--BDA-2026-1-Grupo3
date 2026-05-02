package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Raid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class RaidRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Raid> rowMapper = new RowMapper<Raid>() {
        @Override
        public Raid mapRow(ResultSet rs, int rowNum) throws SQLException {
            Raid entity = new Raid();
            entity.setIdRaid(rs.getInt("id_raid"));
            entity.setNombreRaid(rs.getString("nombre_raid"));
            entity.setNivelMinimoRequerido(rs.getInt("nivel_minimo_requerido"));
            entity.setIlevelMinimoRequerido(rs.getInt("ilevel_minimo_requerido"));
            return entity;
        }
    };

    public List<Raid> findAll() {
        String sql = "SELECT * FROM raid";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Raid findById(Integer id) {
        String sql = "SELECT * FROM raid WHERE id_raid = ?";
        List<Raid> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Raid entity) {
        String sql = "INSERT INTO raid (nombre_raid, nivel_minimo_requerido, ilevel_minimo_requerido) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getNombreRaid(), entity.getNivelMinimoRequerido(), entity.getIlevelMinimoRequerido());
    }

    public int update(Raid entity) {
        String sql = "UPDATE raid SET nombre_raid = ?, nivel_minimo_requerido = ?, ilevel_minimo_requerido = ? WHERE id_raid = ?";
        return jdbcTemplate.update(sql, entity.getNombreRaid(), entity.getNivelMinimoRequerido(), entity.getIlevelMinimoRequerido(), entity.getIdRaid());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM raid WHERE id_raid = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void createMassiveEvent(Integer idClan, Integer idRaid, String fecha) {
    // Procedimiento Almacenado Crea el evento e inserta a todos los 'Raider' automáticamente
    String sql = "CALL generar_raid_e_invitar_raiders(?, ?, ?)";
    jdbcTemplate.update(sql, idClan, idRaid, fecha);
    }

    // Ver calendario y cupos  (Logica no realizada, ejemplo de IA solamente) (Evento asumo)
    public List<Map<String, Object>> getWeeklySchedule() {
    String sql = "SELECT * FROM vista_calendario_raids"; 
    return jdbcTemplate.queryForList(sql);
    }
}

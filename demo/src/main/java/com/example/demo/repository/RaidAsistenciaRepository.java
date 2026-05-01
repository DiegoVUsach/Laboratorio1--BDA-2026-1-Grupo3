package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.RaidAsistencia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RaidAsistenciaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<RaidAsistencia> rowMapper = new RowMapper<RaidAsistencia>() {
        @Override
        public RaidAsistencia mapRow(ResultSet rs, int rowNum) throws SQLException {
            RaidAsistencia entity = new RaidAsistencia();
            entity.setIdAsistencia(rs.getInt("id_asistencia"));
            entity.setIdEvento(rs.getInt("id_evento"));
            entity.setIdPersonaje(rs.getInt("id_personaje"));
            entity.setRolCombateAsignado(rs.getString("rol_combate_asignado"));
            entity.setConfirmado(rs.getString("confirmado"));
            return entity;
        }
    };

    public List<RaidAsistencia> findAll() {
        String sql = "SELECT * FROM raid_asistencia";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public RaidAsistencia findById(Integer id) {
        String sql = "SELECT * FROM raid_asistencia WHERE id_asistencia = ?";
        List<RaidAsistencia> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(RaidAsistencia entity) {
        String sql = "INSERT INTO raid_asistencia (id_evento, id_personaje, rol_combate_asignado, confirmado) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getIdEvento(), entity.getIdPersonaje(), entity.getRolCombateAsignado(), entity.getConfirmado());
    }

    public int update(RaidAsistencia entity) {
        String sql = "UPDATE raid_asistencia SET id_evento = ?, id_personaje = ?, rol_combate_asignado = ?, confirmado = ? WHERE id_asistencia = ?";
        return jdbcTemplate.update(sql, entity.getIdEvento(), entity.getIdPersonaje(), entity.getRolCombateAsignado(), entity.getConfirmado(), entity.getIdAsistencia());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM raid_asistencia WHERE id_asistencia = ?";
        return jdbcTemplate.update(sql, id);
    }
}

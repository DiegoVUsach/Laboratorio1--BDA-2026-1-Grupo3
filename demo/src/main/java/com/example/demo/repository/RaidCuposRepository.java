package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.RaidCupos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RaidCuposRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<RaidCupos> rowMapper = new RowMapper<RaidCupos>() {
        @Override
        public RaidCupos mapRow(ResultSet rs, int rowNum) throws SQLException {
            RaidCupos entity = new RaidCupos();
            entity.setIdEvento(rs.getInt("id_evento"));
            entity.setRolCombate(rs.getString("rol_combate"));
            entity.setCuposTotales(rs.getInt("cupos_totales"));
            entity.setCuposOcupados(rs.getInt("cupos_ocupados"));
            return entity;
        }
    };

    public List<RaidCupos> findAll() {
        String sql = "SELECT * FROM raid_cupos";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public RaidCupos findById(Integer id) {
        String sql = "SELECT * FROM raid_cupos WHERE id_evento = ?";
        List<RaidCupos> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(RaidCupos entity) {
        String sql = "INSERT INTO raid_cupos (rol_combate, cupos_totales, cupos_ocupados) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getRolCombate(), entity.getCuposTotales(), entity.getCuposOcupados());
    }

    public int update(RaidCupos entity) {
        String sql = "UPDATE raid_cupos SET rol_combate = ?, cupos_totales = ?, cupos_ocupados = ? WHERE id_evento = ?";
        return jdbcTemplate.update(sql, entity.getRolCombate(), entity.getCuposTotales(), entity.getCuposOcupados(), entity.getIdEvento());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM raid_cupos WHERE id_evento = ?";
        return jdbcTemplate.update(sql, id);
    }
}

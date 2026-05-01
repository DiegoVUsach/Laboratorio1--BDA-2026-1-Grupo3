package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.AuditoriaLiderazgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AuditoriaLiderazgoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<AuditoriaLiderazgo> rowMapper = new RowMapper<AuditoriaLiderazgo>() {
        @Override
        public AuditoriaLiderazgo mapRow(ResultSet rs, int rowNum) throws SQLException {
            AuditoriaLiderazgo entity = new AuditoriaLiderazgo();
            entity.setIdAudLider(rs.getInt("id_aud_lider"));
            entity.setIdClan(rs.getInt("id_clan"));
            entity.setIdLiderAnterior(rs.getInt("id_lider_anterior"));
            entity.setIdLiderNuevo(rs.getInt("id_lider_nuevo"));
            if (rs.getDate("fecha_cambio") != null) entity.setFechaCambio(rs.getDate("fecha_cambio").toLocalDate());
            return entity;
        }
    };

    public List<AuditoriaLiderazgo> findAll() {
        String sql = "SELECT * FROM auditoria_liderazgo";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public AuditoriaLiderazgo findById(Integer id) {
        String sql = "SELECT * FROM auditoria_liderazgo WHERE id_aud_lider = ?";
        List<AuditoriaLiderazgo> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(AuditoriaLiderazgo entity) {
        String sql = "INSERT INTO auditoria_liderazgo (id_clan, id_lider_anterior, id_lider_nuevo, fecha_cambio) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getIdClan(), entity.getIdLiderAnterior(), entity.getIdLiderNuevo(), entity.getFechaCambio());
    }

    public int update(AuditoriaLiderazgo entity) {
        String sql = "UPDATE auditoria_liderazgo SET id_clan = ?, id_lider_anterior = ?, id_lider_nuevo = ?, fecha_cambio = ? WHERE id_aud_lider = ?";
        return jdbcTemplate.update(sql, entity.getIdClan(), entity.getIdLiderAnterior(), entity.getIdLiderNuevo(), entity.getFechaCambio(), entity.getIdAudLider());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM auditoria_liderazgo WHERE id_aud_lider = ?";
        return jdbcTemplate.update(sql, id);
    }
}

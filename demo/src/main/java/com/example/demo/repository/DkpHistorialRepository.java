package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.DkpHistorial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DkpHistorialRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<DkpHistorial> rowMapper = new RowMapper<DkpHistorial>() {
        @Override
        public DkpHistorial mapRow(ResultSet rs, int rowNum) throws SQLException {
            DkpHistorial entity = new DkpHistorial();
            entity.setIdLog(rs.getInt("id_log"));
            entity.setIdPersonaje(rs.getInt("id_personaje"));
            entity.setCantidad(rs.getInt("cantidad"));
            entity.setMotivo(rs.getString("motivo"));
            if (rs.getDate("fecha") != null) entity.setFecha(rs.getDate("fecha").toLocalDate());
            return entity;
        }
    };

    public List<DkpHistorial> findAll() {
        String sql = "SELECT * FROM dkp_historial";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public DkpHistorial findById(Integer id) {
        String sql = "SELECT * FROM dkp_historial WHERE id_log = ?";
        List<DkpHistorial> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(DkpHistorial entity) {
        String sql = "INSERT INTO dkp_historial (id_personaje, cantidad, motivo, fecha) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getIdPersonaje(), entity.getCantidad(), entity.getMotivo(), entity.getFecha());
    }

    public int update(DkpHistorial entity) {
        String sql = "UPDATE dkp_historial SET id_personaje = ?, cantidad = ?, motivo = ?, fecha = ? WHERE id_log = ?";
        return jdbcTemplate.update(sql, entity.getIdPersonaje(), entity.getCantidad(), entity.getMotivo(), entity.getFecha(), entity.getIdLog());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM dkp_historial WHERE id_log = ?";
        return jdbcTemplate.update(sql, id);
    }
}

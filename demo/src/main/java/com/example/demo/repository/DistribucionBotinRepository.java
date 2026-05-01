package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.DistribucionBotin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DistribucionBotinRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<DistribucionBotin> rowMapper = new RowMapper<DistribucionBotin>() {
        @Override
        public DistribucionBotin mapRow(ResultSet rs, int rowNum) throws SQLException {
            DistribucionBotin entity = new DistribucionBotin();
            entity.setIdDistribucion(rs.getInt("id_distribucion"));
            entity.setIdEvento(rs.getInt("id_evento"));
            entity.setIdPersonaje(rs.getInt("id_personaje"));
            entity.setIdItem(rs.getInt("id_item"));
            entity.setDkpGastado(rs.getInt("dkp_gastado"));
            return entity;
        }
    };

    public List<DistribucionBotin> findAll() {
        String sql = "SELECT * FROM distribucion_botin";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public DistribucionBotin findById(Integer id) {
        String sql = "SELECT * FROM distribucion_botin WHERE id_distribucion = ?";
        List<DistribucionBotin> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(DistribucionBotin entity) {
        String sql = "INSERT INTO distribucion_botin (id_evento, id_personaje, id_item, dkp_gastado) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getIdEvento(), entity.getIdPersonaje(), entity.getIdItem(), entity.getDkpGastado());
    }

    public int update(DistribucionBotin entity) {
        String sql = "UPDATE distribucion_botin SET id_evento = ?, id_personaje = ?, id_item = ?, dkp_gastado = ? WHERE id_distribucion = ?";
        return jdbcTemplate.update(sql, entity.getIdEvento(), entity.getIdPersonaje(), entity.getIdItem(), entity.getDkpGastado(), entity.getIdDistribucion());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM distribucion_botin WHERE id_distribucion = ?";
        return jdbcTemplate.update(sql, id);
    }
}

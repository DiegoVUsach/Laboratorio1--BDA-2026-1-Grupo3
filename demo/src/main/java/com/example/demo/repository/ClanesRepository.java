package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Clanes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ClanesRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Clanes> rowMapper = new RowMapper<Clanes>() {
        @Override
        public Clanes mapRow(ResultSet rs, int rowNum) throws SQLException {
            Clanes entity = new Clanes();
            entity.setIdClan(rs.getInt("id_clan"));
            entity.setIdLider(rs.getInt("id_lider"));
            entity.setNombreClan(rs.getString("nombre_clan"));
            entity.setPuntosTotalesDkp(rs.getInt("puntos_totales_dkp"));
            return entity;
        }
    };

    public List<Clanes> findAll() {
        String sql = "SELECT * FROM clanes";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Clanes findById(Integer id) {
        String sql = "SELECT * FROM clanes WHERE id_clan = ?";
        List<Clanes> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Clanes entity) {
        String sql = "INSERT INTO clanes (id_lider, nombre_clan, puntos_totales_dkp) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getIdLider(), entity.getNombreClan(), entity.getPuntosTotalesDkp());
    }

    public int update(Clanes entity) {
        String sql = "UPDATE clanes SET id_lider = ?, nombre_clan = ?, puntos_totales_dkp = ? WHERE id_clan = ?";
        return jdbcTemplate.update(sql, entity.getIdLider(), entity.getNombreClan(), entity.getPuntosTotalesDkp(), entity.getIdClan());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM clanes WHERE id_clan = ?";
        return jdbcTemplate.update(sql, id);
    }
}

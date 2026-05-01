package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Clase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ClaseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Clase> rowMapper = new RowMapper<Clase>() {
        @Override
        public Clase mapRow(ResultSet rs, int rowNum) throws SQLException {
            Clase entity = new Clase();
            entity.setIdClase(rs.getInt("id_clase"));
            entity.setNombreClase(rs.getString("nombre_clase"));
            return entity;
        }
    };

    public List<Clase> findAll() {
        String sql = "SELECT * FROM clase";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Clase findById(Integer id) {
        String sql = "SELECT * FROM clase WHERE id_clase = ?";
        List<Clase> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Clase entity) {
        String sql = "INSERT INTO clase (nombre_clase) VALUES (?)";
        return jdbcTemplate.update(sql, entity.getNombreClase());
    }

    public int update(Clase entity) {
        String sql = "UPDATE clase SET nombre_clase = ? WHERE id_clase = ?";
        return jdbcTemplate.update(sql, entity.getNombreClase(), entity.getIdClase());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM clase WHERE id_clase = ?";
        return jdbcTemplate.update(sql, id);
    }
}

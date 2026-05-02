package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Lider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LiderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Lider> rowMapper = new RowMapper<Lider>() {
        @Override
        public Lider mapRow(ResultSet rs, int rowNum) throws SQLException {
            Lider entity = new Lider();
            entity.setIdLider(rs.getInt("id_lider"));
            entity.setIdPersonaje(rs.getInt("id_personaje"));
            return entity;
        }
    };

    public List<Lider> findAll() {
        String sql = "SELECT * FROM lider";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Lider findById(Integer id) {
        String sql = "SELECT * FROM lider WHERE id_lider = ?";
        List<Lider> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Lider entity) {
        String sql = "INSERT INTO lider (id_personaje) VALUES (?)";
        return jdbcTemplate.update(sql, entity.getIdPersonaje());
    }

    public int update(Lider entity) {
        String sql = "UPDATE lider SET id_personaje = ? WHERE id_lider = ?";
        return jdbcTemplate.update(sql, entity.getIdPersonaje(), entity.getIdLider());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM lider WHERE id_lider = ?";
        return jdbcTemplate.update(sql, id);
    }
}

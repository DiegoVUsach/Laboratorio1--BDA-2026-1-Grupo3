package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Roles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RolesRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Roles> rowMapper = new RowMapper<Roles>() {
        @Override
        public Roles mapRow(ResultSet rs, int rowNum) throws SQLException {
            Roles entity = new Roles();
            entity.setIdRol(rs.getInt("id_rol"));
            entity.setNombreRol(rs.getString("nombre_rol"));
            return entity;
        }
    };

    public List<Roles> findAll() {
        String sql = "SELECT * FROM roles";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Roles findById(Integer id) {
        String sql = "SELECT * FROM roles WHERE id_rol = ?";
        List<Roles> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Roles findByName(String nombre) {
        String sql = "SELECT * FROM roles WHERE nombre_rol = ?";
        List<Roles> list = jdbcTemplate.query(sql, rowMapper, nombre);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Roles entity) {
        String sql = "INSERT INTO roles (nombre_rol) VALUES (?)";
        return jdbcTemplate.update(sql, entity.getNombreRol());
    }

    public int update(Roles entity) {
        String sql = "UPDATE roles SET nombre_rol = ? WHERE id_rol = ?";
        return jdbcTemplate.update(sql, entity.getNombreRol(), entity.getIdRol());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM roles WHERE id_rol = ?";
        return jdbcTemplate.update(sql, id);
    }
}

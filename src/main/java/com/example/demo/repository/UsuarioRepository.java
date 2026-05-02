package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UsuarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Usuario> rowMapper = new RowMapper<Usuario>() {
        @Override
        public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
            Usuario entity = new Usuario();
            entity.setIdUsuario(rs.getInt("id_usuario"));
            entity.setNombreUsuario(rs.getString("nombre_usuario"));
            entity.setPassword(rs.getString("password"));
            return entity;
        }
    };

    public List<Usuario> findAll() {
        String sql = "SELECT * FROM usuario";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Usuario findById(Integer id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        List<Usuario> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Usuario findByUsername(String username) {
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ?";
        List<Usuario> list = jdbcTemplate.query(sql, rowMapper, username);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Usuario entity) {
        String sql = "INSERT INTO usuario (nombre_usuario, password) VALUES (?, ?)";
        return jdbcTemplate.update(sql, entity.getNombreUsuario(), entity.getPassword());
    }

    public int update(Usuario entity) {
        String sql = "UPDATE usuario SET nombre_usuario = ?, password = ? WHERE id_usuario = ?";
        return jdbcTemplate.update(sql, entity.getNombreUsuario(), entity.getPassword(), entity.getIdUsuario());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        return jdbcTemplate.update(sql, id);
    }

}

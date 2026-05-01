package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Personaje;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PersonajeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Personaje> rowMapper = new RowMapper<Personaje>() {
        @Override
        public Personaje mapRow(ResultSet rs, int rowNum) throws SQLException {
            Personaje entity = new Personaje();
            entity.setIdPersonaje(rs.getInt("id_personaje"));
            entity.setIdUsuario(rs.getInt("id_usuario"));
            entity.setIdClan(rs.getInt("id_clan"));
            entity.setIdClase(rs.getInt("id_clase"));
            entity.setIdRol(rs.getInt("id_rol"));
            entity.setNombrePersonaje(rs.getString("nombre_personaje"));
            entity.setNivel(rs.getInt("nivel"));
            entity.setItemLevel(rs.getString("item_level"));
            entity.setPuntosDkpActuales(rs.getInt("puntos_dkp_actuales"));
            return entity;
        }
    };

    public List<Personaje> findAll() {
        String sql = "SELECT * FROM personaje";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Personaje findById(Integer id) {
        String sql = "SELECT * FROM personaje WHERE id_personaje = ?";
        List<Personaje> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Personaje entity) {
        String sql = "INSERT INTO personaje (id_usuario, id_clan, id_clase, id_rol, nombre_personaje, nivel, item_level, puntos_dkp_actuales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getIdUsuario(), entity.getIdClan(), entity.getIdClase(), entity.getIdRol(), entity.getNombrePersonaje(), entity.getNivel(), entity.getItemLevel(), entity.getPuntosDkpActuales());
    }

    public int update(Personaje entity) {
        String sql = "UPDATE personaje SET id_usuario = ?, id_clan = ?, id_clase = ?, id_rol = ?, nombre_personaje = ?, nivel = ?, item_level = ?, puntos_dkp_actuales = ? WHERE id_personaje = ?";
        return jdbcTemplate.update(sql, entity.getIdUsuario(), entity.getIdClan(), entity.getIdClase(), entity.getIdRol(), entity.getNombrePersonaje(), entity.getNivel(), entity.getItemLevel(), entity.getPuntosDkpActuales(), entity.getIdPersonaje());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM personaje WHERE id_personaje = ?";
        return jdbcTemplate.update(sql, id);
    }
}

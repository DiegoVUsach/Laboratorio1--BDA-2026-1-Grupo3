package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Evento;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EventoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Evento> rowMapper = new RowMapper<Evento>() {
        @Override
        public Evento mapRow(ResultSet rs, int rowNum) throws SQLException {
            Evento entity = new Evento();
            entity.setIdEvento(rs.getInt("id_evento"));
            entity.setIdRaid(rs.getInt("id_raid"));
            entity.setIdClan(rs.getInt("id_clan"));
            entity.setNombreEvento(rs.getString("nombre_evento"));
            entity.setEstado(rs.getString("estado"));
            if (rs.getTimestamp("fecha_hora") != null) entity.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
            return entity;
        }
    };

    public List<Evento> findAll() {
        String sql = "SELECT * FROM evento";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Evento findById(Integer id) {
        String sql = "SELECT * FROM evento WHERE id_evento = ?";
        List<Evento> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Evento entity) {
        String sql = "INSERT INTO evento (id_raid, id_clan, nombre_evento, estado, fecha_hora) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getIdRaid(), entity.getIdClan(), entity.getNombreEvento(), entity.getEstado(), entity.getFechaHora());
    }

    public int update(Evento entity) {
        String sql = "UPDATE evento SET id_raid = ?, id_clan = ?, nombre_evento = ?, estado = ?, fecha_hora = ? WHERE id_evento = ?";
        return jdbcTemplate.update(sql, entity.getIdRaid(), entity.getIdClan(), entity.getNombreEvento(), entity.getEstado(), entity.getFechaHora(), entity.getIdEvento());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM evento WHERE id_evento = ?";
        return jdbcTemplate.update(sql, id);
    }
}

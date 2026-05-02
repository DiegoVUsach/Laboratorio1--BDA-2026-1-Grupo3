package com.example.demo.repository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.tablas.Raid;

@Repository
public class RaidRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Raid> raidMapper = (rs, rowNum) -> {
        Raid r = new Raid();
        r.setIdRaid(rs.getInt("id_raid"));
        r.setNombreRaid(rs.getString("nombre_raid"));
        r.setFechaRaid(rs.getTimestamp("fecha_raid").toLocalDateTime());
        r.setItemLevelMinimo(rs.getInt("item_level_minimo"));
        r.setTanques(rs.getInt("tanques"));
        r.setHealers(rs.getInt("healers"));
        r.setDps(rs.getInt("dps"));
        r.setEstado(rs.getString("estado"));
        return r;
    };

    public List<Raid> findAll() {
        return jdbcTemplate.query("SELECT * FROM raids ORDER BY fecha_raid ASC", raidMapper);
    }

    public int save(Raid r) {
        String sql = "INSERT INTO raids (nombre_raid, fecha_raid, item_level_minimo, tanques, healers, dps) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, r.getNombreRaid(), r.getFechaRaid(), r.getItemLevelMinimo(), r.getTanques(), r.getHealers(), r.getDps());
    }

    public Raid findById(Integer id) {
        String sql = "SELECT * FROM raids WHERE id_raid = ?";
        List<Raid> list = jdbcTemplate.query(sql, raidMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void invitarRaiders(Integer idRaid, Integer idClan) {
    String sql = "CALL sp_invitacion_masiva_raiders(?, ?)";
    jdbcTemplate.update(sql, idRaid, idClan);
}

}
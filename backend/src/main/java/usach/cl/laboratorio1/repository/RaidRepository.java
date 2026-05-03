package usach.cl.laboratorio1.repository;

import usach.cl.laboratorio1.tablas.Raid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<Raid> findAll(int page, int size) {
        String sql = "SELECT * FROM raids ORDER BY fecha_raid ASC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, raidMapper, size, page * size);
    }

    public List<Raid> findAll() {
        return jdbcTemplate.query("SELECT * FROM raids ORDER BY fecha_raid ASC", raidMapper);
    }

    public Raid findById(Integer id) {
        List<Raid> list = jdbcTemplate.query(
                "SELECT * FROM raids WHERE id_raid = ?", raidMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Raid r) {
        String sql = "INSERT INTO raids (nombre_raid, fecha_raid, item_level_minimo, " +
                "tanques, healers, dps) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                r.getNombreRaid(), r.getFechaRaid(), r.getItemLevelMinimo(),
                r.getTanques(), r.getHealers(), r.getDps());
    }

    public int update(Raid r) {
        String sql = "UPDATE raids SET nombre_raid = ?, fecha_raid = ?, " +
                "item_level_minimo = ?, tanques = ?, healers = ?, dps = ?, " +
                "estado = ? WHERE id_raid = ?";
        return jdbcTemplate.update(sql,
                r.getNombreRaid(), r.getFechaRaid(), r.getItemLevelMinimo(),
                r.getTanques(), r.getHealers(), r.getDps(),
                r.getEstado(), r.getIdRaid());
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM raids WHERE id_raid = ?", id);
    }

    // Ejecuta el Stored Procedure 2 (Req 4): invitacion masiva.
    // CALL es la forma de ejecutar un procedimiento almacenado en PostgreSQL.
    // Internamente el SP busca todos los personajes del clan con rol 'Raider'
    // y los inscribe a la raid. El Trigger 1 se activa en cada INSERT.
    public void invitarRaiders(Integer idRaid, Integer idClan) {
        jdbcTemplate.update("CALL sp_invitacion_masiva_raiders(?, ?)", idRaid, idClan);
    }
}
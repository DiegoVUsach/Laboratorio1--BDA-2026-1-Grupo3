package usach.cl.laboratorio1.repository;

import usach.cl.laboratorio1.tablas.Clanes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClanesRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Notar el (Integer) rs.getObject("id_lider") en vez de rs.getInt().
    // ¿Por que? Porque id_lider puede ser NULL (un clan recien creado
    // aun no tiene lider). getInt() convertiria NULL a 0, lo cual es
    // incorrecto. getObject() respeta el null.
    private final RowMapper<Clanes> rowMapper = (rs, rowNum) -> {
        Clanes c = new Clanes();
        c.setIdClan(rs.getInt("id_clan"));
        c.setIdLider((Integer) rs.getObject("id_lider"));
        c.setNombreClan(rs.getString("nombre_clan"));
        return c;
    };

    public List<Clanes> findAll() {
        return jdbcTemplate.query("SELECT * FROM clanes", rowMapper);
    }

    public Clanes findById(Integer id) {
        List<Clanes> list = jdbcTemplate.query(
                "SELECT * FROM clanes WHERE id_clan = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Clanes entity) {
        return jdbcTemplate.update(
                "INSERT INTO clanes (nombre_clan, id_lider) VALUES (?, ?)",
                entity.getNombreClan(), entity.getIdLider());
    }

    // Este UPDATE es el que dispara el Trigger de auditoria (Req 6).
    // Cuando cambia id_lider, PostgreSQL ejecuta trg_auditoria_liderazgo
    // automaticamente y guarda el cambio en auditoria_liderazgo.
    public int update(Clanes entity) {
        return jdbcTemplate.update(
                "UPDATE clanes SET id_lider = ?, nombre_clan = ? WHERE id_clan = ?",
                entity.getIdLider(), entity.getNombreClan(), entity.getIdClan());
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM clanes WHERE id_clan = ?", id);
    }
}
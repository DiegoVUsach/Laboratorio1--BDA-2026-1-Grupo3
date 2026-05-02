package com.example.demo.repository;
import com.example.demo.tablas.Clanes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ClanesRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Clanes> rowMapper = (rs, rowNum) -> {
        Clanes c = new Clanes();
        c.setIdClan(rs.getInt("id_clan"));
        c.setIdLider(rs.getInt("id_lider"));
        c.setNombreClan(rs.getString("nombre_clan"));
        return c;
    };

    public List<Clanes> findAll() {
        return jdbcTemplate.query("SELECT * FROM clanes", rowMapper);
    }

    public Clanes findById(Integer id) {
        String sql = "SELECT * FROM clanes WHERE id_clan = ?";
        List<Clanes> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Clanes entity) {
        String sql = "INSERT INTO clanes (nombre_clan, id_lider) VALUES (?, ?)";
        return jdbcTemplate.update(sql, entity.getNombreClan(), entity.getIdLider());
    }

    public int update(Clanes entity) {
        String sql = "UPDATE clanes SET id_lider = ?, nombre_clan = ? WHERE id_clan = ?";
        return jdbcTemplate.update(sql, entity.getIdLider(), entity.getNombreClan(), entity.getIdClan());
    }
}
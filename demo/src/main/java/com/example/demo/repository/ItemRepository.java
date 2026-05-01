package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.tablas.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Item> rowMapper = new RowMapper<Item>() {
        @Override
        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
            Item entity = new Item();
            entity.setIdItem(rs.getInt("id_item"));
            entity.setNombreItem(rs.getString("nombre_item"));
            entity.setCostoDkp(rs.getInt("costo_dkp"));
            return entity;
        }
    };

    public List<Item> findAll() {
        String sql = "SELECT * FROM item";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Item findById(Integer id) {
        String sql = "SELECT * FROM item WHERE id_item = ?";
        List<Item> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Item entity) {
        String sql = "INSERT INTO item (nombre_item, costo_dkp) VALUES (?, ?)";
        return jdbcTemplate.update(sql, entity.getNombreItem(), entity.getCostoDkp());
    }

    public int update(Item entity) {
        String sql = "UPDATE item SET nombre_item = ?, costo_dkp = ? WHERE id_item = ?";
        return jdbcTemplate.update(sql, entity.getNombreItem(), entity.getCostoDkp(), entity.getIdItem());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM item WHERE id_item = ?";
        return jdbcTemplate.update(sql, id);
    }
}

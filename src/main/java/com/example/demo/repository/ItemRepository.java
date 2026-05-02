package com.example.demo.repository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.tablas.HistorialBotin;
import com.example.demo.tablas.Item;

@Repository
public class ItemRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Item> findAll() {
        return jdbcTemplate.query("SELECT * FROM item", (rs, rowNum) -> {
            Item item = new Item();
            item.setIdItem(rs.getInt("id_item"));
            item.setNombreItem(rs.getString("nombre_item"));
            item.setCostoDkp(rs.getInt("costo_dkp"));
            return item;
        });
    }

    // Método para ejecutar el SP de Repartir Botín
    public void repartirBotin(Integer idPersonaje, Integer idItem, Integer idRaid) {
        String sql = "CALL sp_repartir_botin(?, ?, ?)";
        jdbcTemplate.update(sql, idPersonaje, idItem, idRaid);
    }

    public List<HistorialBotin> obtenerHistorialPorPersonaje(Integer idPersonaje) {
    String sql = "SELECT h.*, i.nombre_item, p.nombre_personaje " +
                 "FROM historial_botin h " +
                 "JOIN item i ON h.id_item = i.id_item " +
                 "JOIN personaje p ON h.id_personaje = p.id_personaje " +
                 "WHERE h.id_personaje = ?";
    
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        HistorialBotin h = new HistorialBotin();
        h.setIdEntrega(rs.getInt("id_entrega"));
        h.setNombreItem(rs.getString("nombre_item"));
        h.setNombrePersonaje(rs.getString("nombre_personaje"));
        h.setFechaEntrega(rs.getTimestamp("fecha_entrega").toLocalDateTime());
        return h;
    }, idPersonaje);
}
}
package usach.cl.laboratorio1.repository;

import usach.cl.laboratorio1.tablas.HistorialBotin;
import usach.cl.laboratorio1.tablas.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Item> itemMapper = (rs, rowNum) -> {
        Item item = new Item();
        item.setIdItem(rs.getInt("id_item"));
        item.setNombreItem(rs.getString("nombre_item"));
        item.setRareza(rs.getString("rareza"));
        item.setCostoDkp(rs.getInt("costo_dkp"));
        return item;
    };

    public List<Item> findAll(int page, int size) {
        String sql = "SELECT * FROM item ORDER BY id_item LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, itemMapper, size, page * size);
    }

    public List<Item> findAll() {
        return jdbcTemplate.query("SELECT * FROM item", itemMapper);
    }

    public Item findById(Integer id) {
        List<Item> list = jdbcTemplate.query(
                "SELECT * FROM item WHERE id_item = ?", itemMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Item item) {
        return jdbcTemplate.update(
                "INSERT INTO item (nombre_item, rareza, costo_dkp) VALUES (?, ?, ?)",
                item.getNombreItem(),
                item.getRareza() != null ? item.getRareza() : "Comun",
                item.getCostoDkp());
    }

    public int update(Item item) {
        return jdbcTemplate.update(
                "UPDATE item SET nombre_item = ?, rareza = ?, costo_dkp = ? " +
                        "WHERE id_item = ?",
                item.getNombreItem(), item.getRareza(),
                item.getCostoDkp(), item.getIdItem());
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM item WHERE id_item = ?", id);
    }

    // Ejecuta el Stored Procedure 1 (Req 3): distribuir botin.
    // CALL sp_repartir_botin hace todo en una transaccion atomica:
    // verifica DKP, descuenta puntos, registra en historial.
    // Si el personaje no tiene DKP suficientes, PostgreSQL
    // lanza una excepcion y nada se modifica.
    public void repartirBotin(Integer idPersonaje, Integer idItem, Integer idRaid) {
        jdbcTemplate.update("CALL sp_repartir_botin(?, ?, ?)",
                idPersonaje, idItem, idRaid);
    }

    // Requerimiento 10: historial de botin de un jugador.
    // Hace JOIN con item y personaje para traer los nombres
    // en vez de solo los IDs (mas legible para el frontend).
    public List<HistorialBotin> obtenerHistorialPorPersonaje(Integer idPersonaje) {
        String sql = "SELECT h.*, i.nombre_item, p.nombre_personaje " +
                "FROM historial_botin h " +
                "JOIN item i ON h.id_item = i.id_item " +
                "JOIN personaje p ON h.id_personaje = p.id_personaje " +
                "WHERE h.id_personaje = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            HistorialBotin h = new HistorialBotin();
            h.setIdEntrega(rs.getInt("id_entrega"));
            h.setIdPersonaje(rs.getInt("id_personaje"));
            h.setIdItem(rs.getInt("id_item"));
            h.setIdRaid(rs.getInt("id_raid"));
            h.setNombreItem(rs.getString("nombre_item"));
            h.setNombrePersonaje(rs.getString("nombre_personaje"));
            h.setFechaEntrega(rs.getTimestamp("fecha_entrega").toLocalDateTime());
            return h;
        }, idPersonaje);
    }

    // Requerimiento 7: refrescar la vista materializada.
    // Llama a la funcion SQL que creamos en script_db.sql.
    public void refrescarRanking() {
        jdbcTemplate.execute("SELECT refrescar_ranking_clan()");
    }

    // Requerimiento 7: obtener el ranking desde la vista materializada.
    // queryForList retorna List<Map<String, Object>> que es una forma
    // generica de devolver filas sin necesitar un RowMapper especifico.
    public List<Map<String, Object>> obtenerRanking() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_ranking_clan");
    }
}
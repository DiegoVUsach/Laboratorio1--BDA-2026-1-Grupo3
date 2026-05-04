package usach.cl.laboratorio1.repository;

import usach.cl.laboratorio1.dto.InventarioItemDTO;
import usach.cl.laboratorio1.tablas.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InventarioItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<InventarioItemDTO> dtoMapper = (rs, rowNum) -> {
        InventarioItemDTO dto = new InventarioItemDTO();
        dto.setIdInventario(rs.getInt("id_inventario"));
        dto.setIdItem(rs.getInt("id_item"));
        dto.setNombreItem(rs.getString("nombre_item"));
        dto.setRareza(rs.getString("rareza"));
        String tipo = rs.getString("tipo");
        if (tipo != null) {
            dto.setTipo(Item.TipoItem.valueOf(tipo));
        }
        dto.setNivel(rs.getInt("nivel"));
        dto.setCostoDkp(rs.getInt("costo_dkp"));
        return dto;
    };

    public List<InventarioItemDTO> findDetalleByInventario(Integer idInventario) {
        String sql = "SELECT ii.id_inventario, ii.id_item, i.nombre_item, i.rareza, " +
                "i.tipo, i.nivel, i.costo_dkp " +
                "FROM inventario_item ii " +
                "JOIN item i ON ii.id_item = i.id_item " +
                "WHERE ii.id_inventario = ? " +
                "ORDER BY ii.id_item";
        return jdbcTemplate.query(sql, dtoMapper, idInventario);
    }

    public List<InventarioItemDTO> findDetalleByPersonaje(Integer idPersonaje) {
        String sql = "SELECT ii.id_inventario, ii.id_item, i.nombre_item, i.rareza, " +
                "i.tipo, i.nivel, i.costo_dkp " +
                "FROM inventario_item ii " +
                "JOIN inventario inv ON ii.id_inventario = inv.id_inventario " +
                "JOIN item i ON ii.id_item = i.id_item " +
                "WHERE inv.id_personaje = ? " +
                "ORDER BY ii.id_item";
        return jdbcTemplate.query(sql, dtoMapper, idPersonaje);
    }

    public int save(Integer idInventario, Integer idItem) {
        String sql = "INSERT INTO inventario_item (id_inventario, id_item) VALUES (?, ?)";
        return jdbcTemplate.update(sql, idInventario, idItem);
    }

    public int delete(Integer idInventario, Integer idItem) {
        String sql = "DELETE FROM inventario_item WHERE id_inventario = ? AND id_item = ?";
        return jdbcTemplate.update(sql, idInventario, idItem);
    }
}

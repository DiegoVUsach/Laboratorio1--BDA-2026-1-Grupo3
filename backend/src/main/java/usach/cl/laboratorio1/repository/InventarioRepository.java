package usach.cl.laboratorio1.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usach.cl.laboratorio1.dto.InventarioDTO;
import usach.cl.laboratorio1.dto.InventarioItemDTO;
import usach.cl.laboratorio1.tablas.Inventario;
import usach.cl.laboratorio1.tablas.Item;

@Repository
public class InventarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

// --- Métodos Auxiliares de Conversión Segura ---
private Long toLong(Object obj) {
    return (obj instanceof Number n) ? n.longValue() : null;
}

private Integer toInt(Object obj) {
    return (obj instanceof Number n) ? n.intValue() : null;
}

// Mapper para la entidad interna (Usa Integer)
private final RowMapper<Inventario> rowMapper = (rs, rowNum) -> {
    Inventario e = new Inventario();
    // Usamos toInt para convertir cualquier tipo numérico de la DB a Integer de forma segura
    e.setIdInventario(toInt(rs.getObject("id_inventario")));
    e.setIdPersonaje(toInt(rs.getObject("id_personaje")));
    
    e.setArmaduraEquipado(toInt(rs.getObject("armadura_equipado")));
    e.setArmaEquipado(toInt(rs.getObject("arma_equipado")));
    e.setAccesorioEquipado(toInt(rs.getObject("accesorio_equipado")));
    return e;
};

// Mapper para el DTO (Usa Long)
private final RowMapper<InventarioDTO> dtoMapper = (rs, rowNum) -> {
    InventarioDTO e = new InventarioDTO();
    
    // Usamos toLong para garantizar que se asigne un Long sin errores de casteo
    e.setIdInventario(toLong(rs.getObject("id_inventario")));
    e.setIdPersonaje(toLong(rs.getObject("id_personaje")));
    
    e.setArmaduraEquipado(toLong(rs.getObject("armadura_equipado")));
    e.setArmaEquipado(toLong(rs.getObject("arma_equipado")));
    e.setAccesorioEquipado(toLong(rs.getObject("accesorio_equipado")));

    e.setNombreArmadura(rs.getString("nombreArmadura"));
    e.setNivelArmadura(toInt(rs.getObject("nivelArmadura")));
    
    e.setNombreArma(rs.getString("nombreArma"));
    e.setNivelArma(toInt(rs.getObject("nivelArma")));
    
    e.setNombreAccesorio(rs.getString("nombreAccesorio"));
    e.setNivelAccesorio(toInt(rs.getObject("nivelAccesorio")));
    
    return e;
};

private final RowMapper<InventarioItemDTO> itemDtoMapper = (rs, rowNum) -> {
    InventarioItemDTO dto = new InventarioItemDTO();
    dto.setIdInventario(rs.getInt("id_inventario"));
    dto.setIdItem(rs.getInt("id_item"));
    dto.setNombreItem(rs.getString("nombre_item"));
    dto.setRareza(rs.getString("rareza"));
    // Mapeo del Enum TipoItem (Asegúrate que coincida con tu entidad Item)
    dto.setTipo(Item.TipoItem.valueOf(rs.getString("tipo"))); 
    dto.setNivel(rs.getInt("nivel"));
    dto.setCostoDkp(rs.getInt("costo_dkp"));
    return dto;
};


public List<InventarioItemDTO> findItemsByPersonaje(Integer idPersonaje) {
    String sql = "SELECT ii.id_inventario, i.* FROM inventario_item ii " +
                 "JOIN item i ON ii.id_item = i.id_item " +
                 "JOIN inventario inv ON ii.id_inventario = inv.id_inventario " +
                 "WHERE inv.id_personaje = ?";
    return jdbcTemplate.query(sql, itemDtoMapper, idPersonaje);
}

// Método para agregar un ítem nuevo a la bolsa (cuando el personaje gana botín)
public int addItemToInventario(Integer idInventario, Integer idItem) {
    String sql = "INSERT INTO inventario_item (id_inventario, id_item) VALUES (?, ?)";
    return jdbcTemplate.update(sql, idInventario, idItem);
}



    private static final String SELECT_INVENTARIO_DETALLE =
    "SELECT e.*, " +
    "ia.nombre_item AS nombreArmadura, ia.nivel AS nivelArmadura, " + // Alias corregido
    "im.nombre_item AS nombreArma, im.nivel AS nivelArma, " +
    "ic.nombre_item AS nombreAccesorio, ic.nivel AS nivelAccesorio " +
    "FROM inventario e " +
    "LEFT JOIN item ia ON e.armadura_equipado = ia.id_item " +
    "LEFT JOIN item im ON e.arma_equipado = im.id_item " +
    "LEFT JOIN item ic ON e.accesorio_equipado = ic.id_item";

    public List<Inventario> findAll(int page, int size) {
        String sql = "SELECT * FROM inventario ORDER BY id_inventario LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, rowMapper, size, page * size);
    }

    public List<InventarioDTO> findAllDetalle(int page, int size) {
        String sql = SELECT_INVENTARIO_DETALLE + " ORDER BY e.id_inventario LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, dtoMapper, size, page * size);
    }

    public Inventario findById(Integer id) {
        List<Inventario> list = jdbcTemplate.query(
                "SELECT * FROM inventario WHERE id_inventario = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public InventarioDTO findDetalleById(Integer id) {
        List<InventarioDTO> list = jdbcTemplate.query(
                SELECT_INVENTARIO_DETALLE + " WHERE e.id_inventario = ?", dtoMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Inventario findByPersonaje(Integer idPersonaje) {
        List<Inventario> list = jdbcTemplate.query(
                "SELECT * FROM inventario WHERE id_personaje = ?", rowMapper, idPersonaje);
        return list.isEmpty() ? null : list.get(0);
    }

    public InventarioDTO findDetalleByPersonaje(Integer idPersonaje) {
        List<InventarioDTO> list = jdbcTemplate.query(
                SELECT_INVENTARIO_DETALLE + " WHERE e.id_personaje = ?", dtoMapper, idPersonaje);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Inventario e) {
        String sql = "INSERT INTO inventario " +
            "(id_personaje, armadura_equipado, arma_equipado, accesorio_equipado) " +
                "VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
            e.getIdPersonaje(), e.getArmaduraEquipado(), e.getArmaEquipado(),
            e.getAccesorioEquipado());
    }

    public int update(Inventario e) {
        String sql = "UPDATE inventario SET id_personaje = ?, armadura_equipado = ?, " +
            "arma_equipado = ?, accesorio_equipado = ? " +
                "WHERE id_inventario = ?";
        return jdbcTemplate.update(sql,
            e.getIdPersonaje(), e.getArmaduraEquipado(), e.getArmaEquipado(),
            e.getAccesorioEquipado(), e.getIdInventario());
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM inventario WHERE id_inventario = ?", id);
    }
}

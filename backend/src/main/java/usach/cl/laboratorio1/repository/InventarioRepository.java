package usach.cl.laboratorio1.repository;

import usach.cl.laboratorio1.dto.InventarioDTO;
import usach.cl.laboratorio1.tablas.Inventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InventarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Inventario> rowMapper = (rs, rowNum) -> {
        Inventario e = new Inventario();
        e.setIdInventario(rs.getInt("id_inventario"));
        e.setIdPersonaje(rs.getInt("id_personaje"));
        e.setArmaduraEquipado((Integer) rs.getObject("armadura_equipado"));
        e.setArmaEquipado((Integer) rs.getObject("arma_equipado"));
        e.setAccesorioEquipado((Integer) rs.getObject("accesorio_equipado"));
        return e;
    };

    private final RowMapper<InventarioDTO> dtoMapper = (rs, rowNum) -> {
        InventarioDTO e = new InventarioDTO();
        e.setIdInventario(rs.getInt("id_inventario"));
        e.setIdPersonaje(rs.getInt("id_personaje"));
        e.setArmaduraEquipado((Integer) rs.getObject("armadura_equipado"));
        e.setArmaEquipado((Integer) rs.getObject("arma_equipado"));
        e.setAccesorioEquipado((Integer) rs.getObject("accesorio_equipado"));
        e.setNombreArmadura(rs.getString("nombre_armadura"));
        e.setNombreArma(rs.getString("nombre_arma"));
        e.setNombreAccesorio(rs.getString("nombre_accesorio"));
        return e;
    };

    private static final String SELECT_INVENTARIO_DETALLE =
            "SELECT e.*, ia.nombre_item AS nombre_armadura, " +
            "im.nombre_item AS nombre_arma, " +
            "ic.nombre_item AS nombre_accesorio " +
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

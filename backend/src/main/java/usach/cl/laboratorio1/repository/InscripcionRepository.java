package usach.cl.laboratorio1.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import usach.cl.laboratorio1.tablas.InscripcionRaid;

@Repository
public class InscripcionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Al ejecutar este INSERT, el Trigger 1 (trg_validar_item_level)
    // se dispara automaticamente en PostgreSQL.
    // Si el personaje no tiene suficiente item_level, PostgreSQL
    // lanza una excepcion y el INSERT se cancela.
    // Esa excepcion sube hasta el controller como un error 400.
        public int inscribir(InscripcionRaid ins) {
        String sql = "INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid) " +
                        "VALUES (?, ?, ?) " +
                        "ON CONFLICT (id_raid, id_personaje) " +
                        "DO UPDATE SET rol_en_raid = EXCLUDED.rol_en_raid"; 
        return jdbcTemplate.update(sql, 
                ins.getIdRaid(), ins.getIdPersonaje(), ins.getRolEnRaid());
        }

    // Cuenta cuantos inscritos hay en una raid para un rol especifico.
    // Se usa para calcular los cupos libres en el calendario (Req 9).
    // Ejemplo: si la raid pide 2 tanques y este metodo retorna 1,
    // entonces queda 1 cupo de tanque libre.
    public Integer contarInscritosPorRol(Integer idRaid, String rol) {
        String sql = "SELECT COUNT(*) FROM inscripciones_raid " +
                "WHERE id_raid = ? AND rol_en_raid = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, idRaid, rol);
    }

    // El Guild Master confirma la asistencia de un jugador.
    // Cambia el campo 'confirmado' de FALSE a TRUE.
    // Solo los confirmados cuentan en el ranking (vista materializada).
    public int confirmar(Integer idInscripcion) {
        return jdbcTemplate.update(
                "UPDATE inscripciones_raid SET confirmado = true " +
                        "WHERE id_inscripcion = ?", idInscripcion);
    }
    public List<Integer> obtenerIdsParticipantes(Integer idRaid) {
    String sql = "SELECT id_personaje FROM inscripciones_raid WHERE id_raid = ?";
    return jdbcTemplate.queryForList(sql, Integer.class, idRaid);
}

    // Lista los inscritos de una raid con nombre y estado de confirmacion.
    // Se usa en el frontend para que el GM confirme asistencias.
    public List<Map<String, Object>> findByRaid(Integer idRaid) {
        String sql = "SELECT ir.id_inscripcion, ir.id_personaje, p.nombre_personaje, " +
                     "ir.rol_en_raid, ir.confirmado " +
                     "FROM inscripciones_raid ir JOIN personaje p ON ir.id_personaje = p.id_personaje " +
                     "WHERE ir.id_raid = ? ORDER BY ir.rol_en_raid";
        return jdbcTemplate.queryForList(sql, idRaid);
    }
}
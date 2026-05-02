package com.example.demo.repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.tablas.InscripcionRaid;

@Repository
public class InscripcionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int inscribir(InscripcionRaid ins) {
        // Al ejecutar este INSERT, el Trigger 1 de SQL saltará si el ilvl es bajo
        String sql = "INSERT INTO inscripciones_raid (id_raid, id_personaje, rol_en_raid) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, ins.getIdRaid(), ins.getIdPersonaje(), ins.getRolEnRaid());
    }

    // Para el requerimiento 9 (Cupos disponibles)
    public Integer contarInscritosPorRol(Integer idRaid, String rol) {
        String sql = "SELECT COUNT(*) FROM inscripciones_raid WHERE id_raid = ? AND rol_en_raid = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, idRaid, rol);
    }

    public int confirmar(Integer idInscripcion) {
        String sql = "UPDATE inscripciones_raid SET confirmado = true WHERE id_inscripcion = ?";
        return jdbcTemplate.update(sql, idInscripcion);
    }
    
}
package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.RaidAsistenciaService;
import com.example.demo.tablas.RaidAsistencia;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin("*")
public class RaidAsistenciaController {

    @Autowired
    private RaidAsistenciaService raidAsistenciaService;

    @PostMapping("/unirse")
    public ResponseEntity<?> signUp(@RequestBody RaidAsistencia asistencia, Authentication auth) {
        try {
            raidAsistenciaService.signUp(auth.getName(), asistencia);
            return ResponseEntity.ok("Inscripción realizada. Sujeta a validación de equipo (Trigger).");
        } catch (Exception e) {
            // Aqui errores de Java como los del Trigger de la BD
            return ResponseEntity.badRequest().body("Error al inscribirse: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirm(@PathVariable Integer id, Authentication auth) {
        try {
            raidAsistenciaService.confirmAttendance(auth.getName(), id);
            return ResponseEntity.ok("Asistencia confirmada.");
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
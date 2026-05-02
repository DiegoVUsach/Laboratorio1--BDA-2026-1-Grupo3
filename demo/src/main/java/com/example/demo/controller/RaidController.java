package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.RaidRepository;
import com.example.demo.service.RaidService;

@RestController
@RequestMapping("/api/raids")
@CrossOrigin("*")
public class RaidController {

    @Autowired
    private RaidService raidService;

    @Autowired
    private RaidRepository raidRepository;
    
    // Generar evento e invitar masivamente
    @PostMapping("/evento-masivo")
    public ResponseEntity<?> createMassiveEvent(
            @RequestBody MassiveEventRequest req,
            Authentication auth) {
        try {
            raidService.scheduleMassiveRaid(auth.getName(), req.getIdEjecutor(), req.getIdRaid(), req.getFecha());
            return ResponseEntity.ok("Evento creado e invitaciones enviadas a todos los Raiders.");
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // Endpoint para ver calendario de la semana (Hay que mejorar esto, esta expresado mas que hecho)
    @GetMapping("/calendario")
    public ResponseEntity<?> getCalendar() {
        return ResponseEntity.ok(raidRepository.getWeeklySchedule());
    }


// DTO para la peticion masiva
    public static class MassiveEventRequest {
        private Integer idEjecutor;
        private Integer idRaid;
        private String fecha;
    // Getters y Setters
        public Integer getIdEjecutor() {
            return idEjecutor;
        }
        public void setIdEjecutor(Integer idEjecutor) {
            this.idEjecutor = idEjecutor;
        }
        public Integer getIdRaid() {
            return idRaid;
        }
        public void setIdRaid(Integer idRaid) {
            this.idRaid = idRaid;
        }
        public String getFecha() {
            return fecha;
        }
        public void setFecha(String fecha) {
            this.fecha = fecha;
        }
    }
}

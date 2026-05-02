package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RaidDTO;
import com.example.demo.service.InscripcionService;
import com.example.demo.service.RaidService;
import com.example.demo.tablas.InscripcionRaid;

@RestController
@RequestMapping("/api/raids")
@CrossOrigin("*")
public class RaidController {
    @Autowired
    private RaidService raidService;
    @Autowired
    private InscripcionService inscripcionService;

    @GetMapping("/calendario")
    public List<RaidDTO> verCalendario() {
        return raidService.getCalendarioSemanal();
    }

    @PostMapping("/inscribirse")
    public ResponseEntity<?> inscribirse(
            @RequestBody InscripcionRaid inscripcion, 
            Authentication auth) {
        try {
            inscripcionService.anotarPersonaje(inscripcion, auth.getName());
            return ResponseEntity.ok("Inscripción enviada.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
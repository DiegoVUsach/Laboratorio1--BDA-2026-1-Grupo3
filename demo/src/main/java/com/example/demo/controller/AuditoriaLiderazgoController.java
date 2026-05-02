package com.example.demo.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AuditoriaLiderazgoService;
import com.example.demo.tablas.AuditoriaLiderazgo;


@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin("*")
public class AuditoriaLiderazgoController {

@Autowired
    private AuditoriaLiderazgoService auditoriaLiderazgoService;

    // Endpoint protegido: Solo el Guild Master
    @GetMapping("/liderazgo")
    @PreAuthorize("hasRole('GUILD_MASTER')") 
    public ResponseEntity<List<AuditoriaLiderazgo>> getHistorialLiderazgo() {
        List<AuditoriaLiderazgo> historial = auditoriaLiderazgoService.obtenerHistorialAuditoria();
        return ResponseEntity.ok(historial);
    }
}

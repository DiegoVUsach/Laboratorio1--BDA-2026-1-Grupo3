package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ClanesService;
import com.example.demo.tablas.Clanes;

@RestController
@RequestMapping("/api/clanes")
@CrossOrigin("*")
public class ClanesController {

    @Autowired
    private ClanesService clanesService;

    @GetMapping
    public List<Clanes> findAll() {
        return clanesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clanes> findById(@PathVariable Integer id) {
        Clanes clan = clanesService.findById(id);
        return clan != null ? ResponseEntity.ok(clan) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Clanes clan) {
        clanesService.save(clan);
        return ResponseEntity.ok("Clan created successfully");
    }

    // Endpoint para transferir el mando del clan
    @PutMapping("/{id}/transfer-leadership")
    public ResponseEntity<?> transferLeadership(
            @PathVariable Integer id, 
            @RequestBody TransferRequest request,
            Authentication auth) {
        try {
            clanesService.transferLeadership(
                auth.getName(), 
                id, 
                request.getIdCurrentLeader(), 
                request.getIdNewLeader()
            );
            return ResponseEntity.ok("Liderazgo transferido exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // DTO para la petición
    public static class TransferRequest {
        private Integer idCurrentLeader;
        private Integer idNewLeader;
        // Getters and Setters
        public Integer getIdCurrentLeader() {
            return idCurrentLeader;
        }
        public void setIdCurrentLeader(Integer idCurrentLeader) {
            this.idCurrentLeader = idCurrentLeader;
        }
        public Integer getIdNewLeader() {
            return idNewLeader;
        }
        public void setIdNewLeader(Integer idNewLeader) {
            this.idNewLeader = idNewLeader;
        }
    }
}
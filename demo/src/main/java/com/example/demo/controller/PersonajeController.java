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

import com.example.demo.service.PersonajeService;
import com.example.demo.tablas.Personaje;

@RestController
@RequestMapping("/api/personajes")
@CrossOrigin("*")
public class PersonajeController {

    @Autowired
    private PersonajeService personajeService;

    // Listar todos
    @GetMapping
    public List<Personaje> listar() {
        return personajeService.findAll();
    }

    // Obtener un personaje por ID
    @GetMapping("/{id}")
    public ResponseEntity<Personaje> findById(@PathVariable Integer id) {
        Personaje p = personajeService.findById(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    // Crear Personaje
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Personaje personaje) {
        personajeService.save(personaje);
        return ResponseEntity.ok("Personaje creado exitosamente");
    }

    // Guild Master asigna roles
    // El JSON enviado debe tener: { "idEjecutor": 5, "idObjetivo": 10, "idNuevoRol": 3 }
    @PutMapping("/asignar-rol")
    public ResponseEntity<?> newRol(
            @RequestBody RolRequest request,
            Authentication auth // username del JWT
    ) {
        try {
            String usernameActual = auth.getName();
            personajeService.newRol(
                usernameActual, 
                request.getIdEjecutor(), 
                request.getIdObjetivo(), 
                request.getIdNuevoRol()
            );
            return ResponseEntity.ok("Rol actualizado correctamente por el Guild Master");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // DTO interno para la petición de cambio de rol. Puede hacerse no interno
    public static class RolRequest {
        private Integer idEjecutor; // El PJ que intenta mandar
        private Integer idObjetivo; // Al que le cambian el rol
        private Integer idNuevoRol;
        // Getters y Setters
        public Integer getIdEjecutor() { return idEjecutor; }
        public Integer getIdObjetivo() { return idObjetivo; }
        public Integer getIdNuevoRol() { return idNuevoRol; }
    }
}
package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.PersonajeService;

import lombok.Data;


@RestController
@RequestMapping("/api/personajes")
@CrossOrigin("*") 
public class PersonajeController {

    @Autowired
    private PersonajeService personajeService;

    @PutMapping("/asignar-rol")
    public ResponseEntity<?> asignarRol(@RequestBody RolRequest request, Authentication auth) {
        try {
            personajeService.asignarNuevoRol(auth.getName(), request.getIdEjecutor(), 
                                             request.getIdObjetivo(), request.getNuevoRol());
            return ResponseEntity.ok("Rol actualizado a " + request.getNuevoRol());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

@Data
class RolRequest {
    private Integer idEjecutor;
    private Integer idObjetivo;
    private String nuevoRol; // "Raider", "Member"
}
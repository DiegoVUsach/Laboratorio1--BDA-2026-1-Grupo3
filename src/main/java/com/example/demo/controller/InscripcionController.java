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

import com.example.demo.repository.InscripcionRepository;
import com.example.demo.tablas.InscripcionRaid;


@RestController
@RequestMapping("/api/inscripciones")
@CrossOrigin("*")
public class InscripcionController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    // Endpoint para que un jugador se anote a una Raid (Req. 2)
    @PostMapping("/anotarse")
    public ResponseEntity<?> inscribir(@RequestBody InscripcionRaid inscripcion, Authentication auth) {
        try {
            // Aquí podrías validar con el 'auth' que el personaje le pertenece al usuario
            // pero la lógica de nivel de equipo (Item Level) la validará el TRIGGER 1 en la BD.
            
            inscripcionRepository.inscribir(inscripcion);
            return ResponseEntity.ok("Inscripción realizada con éxito. Esperando confirmación.");
        } catch (Exception e) {
            // Si el Trigger 1 lanza una excepción por bajo nivel, caerá aquí
            return ResponseEntity.status(400).body("Error al inscribirse: " + e.getMessage());
        }
    }

    // Endpoint para que el Guild Master confirme la asistencia (Req. 2)
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarAsistencia(@PathVariable Integer id, Authentication auth) {
        // Lógica para cambiar el booleano 'confirmado' a TRUE
        inscripcionRepository.confirmar(id);
        return ResponseEntity.ok("Asistencia confirmada.");
    }
}
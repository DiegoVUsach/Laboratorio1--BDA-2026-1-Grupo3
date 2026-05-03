package usach.cl.laboratorio1.controller;

import usach.cl.laboratorio1.repository.InscripcionRepository;
import usach.cl.laboratorio1.tablas.InscripcionRaid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscripciones")
@CrossOrigin("*")
public class InscripcionController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    // POST /api/inscripciones/anotarse
    // Inscribir un personaje a una raid.
    // El Trigger 1 en PostgreSQL valida el item_level automaticamente.
    @PostMapping("/anotarse")
    public ResponseEntity<?> inscribir(@RequestBody InscripcionRaid inscripcion,
                                       Authentication auth) {
        try {
            inscripcionRepository.inscribir(inscripcion);
            return ResponseEntity.ok("Inscripcion realizada con exito.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al inscribirse: " + e.getMessage());
        }
    }

    // PUT /api/inscripciones/1/confirmar
    // El Guild Master confirma la asistencia de un jugador.
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarAsistencia(@PathVariable Integer id) {
        inscripcionRepository.confirmar(id);
        return ResponseEntity.ok("Asistencia confirmada.");
    }
}
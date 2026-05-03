package usach.cl.laboratorio1.controller;

import usach.cl.laboratorio1.dto.RaidDTO;
import usach.cl.laboratorio1.repository.RaidRepository;
import usach.cl.laboratorio1.service.InscripcionService;
import usach.cl.laboratorio1.service.RaidService;
import usach.cl.laboratorio1.tablas.InscripcionRaid;
import usach.cl.laboratorio1.tablas.Raid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raids")
@CrossOrigin("*")
public class RaidController {

    @Autowired
    private RaidService raidService;

    @Autowired
    private RaidRepository raidRepository;

    @Autowired
    private InscripcionService inscripcionService;

    // GET /api/raids - Listar todas las raids
    @GetMapping
    public List<Raid> findAll() {
        return raidRepository.findAll();
    }

    // GET /api/raids/1 - Obtener raid por ID
    @GetMapping("/{id}")
    public ResponseEntity<Raid> findById(@PathVariable Integer id) {
        Raid r = raidRepository.findById(id);
        return r != null ? ResponseEntity.ok(r) : ResponseEntity.notFound().build();
    }

    // POST /api/raids - Crear una raid nueva
    // El Guild Master o admin crea eventos de raid (Req 2)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Raid raid) {
        try {
            raidRepository.save(raid);
            return ResponseEntity.ok("Raid creada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT /api/raids/1 - Actualizar una raid
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Raid raid) {
        raid.setIdRaid(id);
        raidRepository.update(raid);
        return ResponseEntity.ok("Raid actualizada");
    }

    // DELETE /api/raids/1 - Eliminar una raid
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        raidRepository.deleteById(id);
        return ResponseEntity.ok("Raid eliminada");
    }

    // GET /api/raids/calendario
    // Requerimiento 9: calendario de raids con cupos disponibles.
    // Devuelve cada raid con los cupos libres por rol (Tanque, Healer, DPS).
    @GetMapping("/calendario")
    public List<RaidDTO> verCalendario() {
        return raidService.getCalendarioSemanal();
    }

    // POST /api/raids/inscribirse
    // Un jugador inscribe su personaje a una raid.
    // Validaciones en Java: personaje pertenece al usuario, raid activa.
    // Validacion en PostgreSQL: Trigger 1 verifica item_level (Req 5).
    @PostMapping("/inscribirse")
    public ResponseEntity<?> inscribirse(@RequestBody InscripcionRaid inscripcion,
                                         Authentication auth) {
        try {
            inscripcionService.anotarPersonaje(inscripcion, auth.getName());
            return ResponseEntity.ok("Inscripcion enviada.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/raids/invitar-raiders
    // Requerimiento 4: Stored Procedure 2 - invitacion masiva.
    // Inscribe automaticamente a todos los Raiders del clan a la raid.
    @PostMapping("/invitar-raiders")
    public ResponseEntity<?> invitarRaiders(@RequestBody InvitarRequest request) {
        try {
            raidRepository.invitarRaiders(request.idRaid, request.idClan);
            return ResponseEntity.ok("Invitaciones masivas enviadas.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    static class InvitarRequest {
        public Integer idRaid;
        public Integer idClan;
    }
}
package usach.cl.laboratorio1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import usach.cl.laboratorio1.dto.RaidDTO;
import usach.cl.laboratorio1.repository.PersonajeRepository;
import usach.cl.laboratorio1.repository.RaidRepository;
import usach.cl.laboratorio1.repository.UsuarioRepository;
import usach.cl.laboratorio1.service.InscripcionService;
import usach.cl.laboratorio1.service.RaidService;
import usach.cl.laboratorio1.tablas.InscripcionRaid;
import usach.cl.laboratorio1.tablas.Personaje;
import usach.cl.laboratorio1.tablas.Raid;

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

    @Autowired
    private PersonajeRepository personajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // FIX BUG 7: paginacion
    @GetMapping
    public List<Raid> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return raidRepository.findAll(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Raid> findById(@PathVariable Integer id) {
        Raid r = raidRepository.findById(id);
        return r != null ? ResponseEntity.ok(r) : ResponseEntity.notFound().build();
    }

    // FIX BUG 2: Solo un Guild Master puede crear raids (Req 2).
    // Se verifica que el usuario tenga al menos un personaje con rol GM.
    @PostMapping
public ResponseEntity<?> create(@RequestBody Raid raid, Authentication auth) {
    try {
        // 1. Buscamos al personaje del usuario que es GM
        Personaje gm = personajeRepository.findByUsuario(auth.getName()).stream()
                .filter(p -> "Guild Master".equals(p.getRolClan()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No tienes un personaje GM"));

        // 2. Guardamos la raid (obtenemos el ID generado)
        Integer idRaidGenerado = raidRepository.save(raid); 

        // 3. ¡MAGIA! Llamamos al SP que ya creaste en SQL
        raidRepository.invitarRaiders(idRaidGenerado, gm.getIdClan());

        return ResponseEntity.ok("Raid creada e invitaciones automáticas enviadas a los Raiders.");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    }

    // FIX BUG 10: Solo GM puede actualizar raids
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody Raid raid,
                                    Authentication auth) {
        if (!esGuildMaster(auth.getName())) {
            return ResponseEntity.status(403).body("Solo el Guild Master puede modificar raids.");
        }
        raid.setIdRaid(id);
        raidRepository.update(raid);
        return ResponseEntity.ok("Raid actualizada");
    }

    // FIX BUG 3: Solo GM puede eliminar raids
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, Authentication auth) {
        if (!esGuildMaster(auth.getName())) {
            return ResponseEntity.status(403).body("Solo el Guild Master puede eliminar raids.");
        }
        raidRepository.deleteById(id);
        return ResponseEntity.ok("Raid eliminada");
    }

    // Req 9: Calendario con cupos
    @GetMapping("/calendario")
    public List<RaidDTO> verCalendario() {
        return raidService.getCalendarioSemanal();
    }

    // Inscribirse a una raid (Trigger 1 valida item level)
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

    // FIX BUG 5: Solo el GM del clan puede invitar raiders masivamente.
    @PostMapping("/invitar-raiders")
    public ResponseEntity<?> invitarRaiders(@RequestBody InvitarRequest request,
                                            Authentication auth) {
        try {
            // Verificar que el usuario tiene un personaje que es lider de ese clan
            List<Personaje> misPersonajes = personajeRepository.findByUsuario(auth.getName());
            boolean esLider = misPersonajes.stream()
                    .anyMatch(p -> personajeRepository.esLiderDeClan(p.getIdPersonaje(), request.idClan));
            if (!esLider) {
                return ResponseEntity.status(403).body("Solo el Guild Master del clan puede invitar raiders.");
            }
            raidRepository.invitarRaiders(request.idRaid, request.idClan);
            return ResponseEntity.ok("Invitaciones masivas enviadas.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Metodo auxiliar: verifica si el usuario tiene al menos un personaje GM
    private boolean esGuildMaster(String username) {
        List<Personaje> personajes = personajeRepository.findByUsuario(username);
        return personajes.stream().anyMatch(p -> "Guild Master".equals(p.getRolClan()));
    }

    static class InvitarRequest {
        public Integer idRaid;
        public Integer idClan;
    }
    @DeleteMapping("/desinscribirse")
    public ResponseEntity<?> desinscribirse(@RequestParam Integer idRaid, 
                                        @RequestParam Integer idPersonaje, 
                                        Authentication auth) {
    try {
        // Validación de seguridad: El usuario autenticado debe ser dueño del personaje
        List<Personaje> misPersonajes = personajeRepository.findByUsuario(auth.getName());
        boolean esMio = misPersonajes.stream().anyMatch(p -> p.getIdPersonaje().equals(idPersonaje));
        
        if (!esMio) {
            return ResponseEntity.status(403).body("No puedes desinscribir a un personaje que no te pertenece.");
        }

        // Aquí debes llamar a un método en tu repository que haga: 
        // DELETE FROM inscripciones WHERE id_raid = ? AND id_personaje = ?
        raidRepository.eliminarInscripcion(idRaid, idPersonaje);
        
        return ResponseEntity.ok("Inscripción anulada.");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
};
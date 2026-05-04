package usach.cl.laboratorio1.controller;

import usach.cl.laboratorio1.service.ClanesService;
import usach.cl.laboratorio1.tablas.Clanes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// FIX BUG 9: CRUD completo de clanes
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
        return ResponseEntity.ok("Clan creado exitosamente");
    }

    // FIX BUG 9: Update de clan (solo datos basicos, no liderazgo)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody Clanes clan) {
        Clanes existing = clanesService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setNombreClan(clan.getNombreClan());
        clanesService.update(existing);
        return ResponseEntity.ok("Clan actualizado");
    }

    // Transferir liderazgo (dispara Trigger 2)
    @PutMapping("/{id}/transfer-leadership")
    public ResponseEntity<?> transferLeadership(
            @PathVariable Integer id,
            @RequestBody TransferRequest request,
            Authentication auth) {
        try {
            clanesService.transferLeadership(auth.getName(), id,
                    request.idCurrentLeader, request.idNewLeader);
            return ResponseEntity.ok("Liderazgo transferido exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // DELETE /api/clanes/{id} - Eliminar un clan
    // Solo el lider del clan puede eliminarlo (validacion JWT).
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, Authentication auth) {
        try {
            clanesService.delete(auth.getName(), id);
            return ResponseEntity.ok("Clan eliminado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    static class TransferRequest {
        public Integer idCurrentLeader;
        public Integer idNewLeader;
    }
}
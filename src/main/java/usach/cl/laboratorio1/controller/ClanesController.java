package usach.cl.laboratorio1.controller;

import usach.cl.laboratorio1.service.ClanesService;
import usach.cl.laboratorio1.tablas.Clanes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clanes")
@CrossOrigin("*")
public class ClanesController {

    @Autowired
    private ClanesService clanesService;

    // GET /api/clanes - Listar todos los clanes
    @GetMapping
    public List<Clanes> findAll() {
        return clanesService.findAll();
    }

    // GET /api/clanes/1 - Obtener un clan por ID
    @GetMapping("/{id}")
    public ResponseEntity<Clanes> findById(@PathVariable Integer id) {
        Clanes clan = clanesService.findById(id);
        return clan != null ? ResponseEntity.ok(clan) : ResponseEntity.notFound().build();
    }

    // POST /api/clanes - Crear un clan nuevo
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Clanes clan) {
        clanesService.save(clan);
        return ResponseEntity.ok("Clan creado exitosamente");
    }

    // PUT /api/clanes/1/transfer-leadership
    // Transferir liderazgo del clan (Req 2 + Req 6).
    // Este endpoint dispara el Trigger de auditoria en PostgreSQL.
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

    // Mini-DTO para la transferencia de liderazgo
    static class TransferRequest {
        public Integer idCurrentLeader;
        public Integer idNewLeader;
    }
}
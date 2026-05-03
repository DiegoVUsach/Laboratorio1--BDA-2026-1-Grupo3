package usach.cl.laboratorio1.controller;

import usach.cl.laboratorio1.repository.PersonajeRepository;
import usach.cl.laboratorio1.service.PersonajeService;
import usach.cl.laboratorio1.tablas.Personaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CRUD completo de Personajes (Requerimiento 1)
// + Asignacion de roles por el Guild Master (Requerimiento 2)
@RestController
@RequestMapping("/api/personajes")
@CrossOrigin("*")
public class PersonajeController {

    @Autowired
    private PersonajeRepository personajeRepository;

    @Autowired
    private PersonajeService personajeService;

    // GET /api/personajes - Listar todos los personajes
    @GetMapping
    public List<Personaje> findAll() {
        return personajeRepository.findAll();
    }

    // GET /api/personajes/3 - Obtener un personaje por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Personaje> findById(@PathVariable Integer id) {
        Personaje p = personajeRepository.findById(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    // GET /api/personajes/mis-personajes
    // Devuelve solo los personajes del usuario autenticado (Req 2).
    // auth.getName() extrae el username del token JWT.
    @GetMapping("/mis-personajes")
    public List<Personaje> misPersonajes(Authentication auth) {
        return personajeRepository.findByUsuario(auth.getName());
    }

    // POST /api/personajes - Crear un personaje nuevo
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Personaje personaje) {
        try {
            personajeRepository.save(personaje);
            return ResponseEntity.ok("Personaje creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT /api/personajes/3 - Actualizar un personaje
    // Solo el dueno del personaje puede modificarlo (validacion JWT).
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody Personaje personaje,
                                    Authentication auth) {
        try {
            if (!personajeRepository.perteneceAUsuario(id, auth.getName())) {
                return ResponseEntity.status(403).body("No tienes permiso sobre este personaje.");
            }
            personaje.setIdPersonaje(id);
            personajeRepository.update(personaje);
            return ResponseEntity.ok("Personaje actualizado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // DELETE /api/personajes/3 - Eliminar un personaje
    // Solo el dueno puede eliminarlo.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, Authentication auth) {
        if (!personajeRepository.perteneceAUsuario(id, auth.getName())) {
            return ResponseEntity.status(403).body("No tienes permiso sobre este personaje.");
        }
        personajeRepository.deleteById(id);
        return ResponseEntity.ok("Personaje eliminado");
    }

    // PUT /api/personajes/asignar-rol
    // Solo el Guild Master puede cambiar roles (Req 2).
    // Recibe: idEjecutor (quien lo hace), idObjetivo (a quien),
    // nuevoRol ("Raider", "Member", etc.)
    @PutMapping("/asignar-rol")
    public ResponseEntity<?> asignarRol(@RequestBody RolRequest request,
                                        Authentication auth) {
        try {
            personajeService.asignarNuevoRol(auth.getName(),
                    request.idEjecutor, request.idObjetivo, request.nuevoRol);
            return ResponseEntity.ok("Rol actualizado a " + request.nuevoRol);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Clase interna para recibir los datos de asignacion de rol.
    // Es como un mini-DTO pero solo se usa en este controller.
    static class RolRequest {
        public Integer idEjecutor;
        public Integer idObjetivo;
        public String nuevoRol;
    }
}
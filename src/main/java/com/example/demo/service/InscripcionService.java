package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.repository.InscripcionRepository;
import com.example.demo.repository.PersonajeRepository;
import com.example.demo.repository.RaidRepository;
import com.example.demo.tablas.InscripcionRaid;
import com.example.demo.tablas.Personaje;
import com.example.demo.tablas.Raid;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private PersonajeRepository personajeRepository;

    @Autowired
    private RaidRepository raidRepository;

    // Lógica para que un usuario se anote con uno de sus personajes
    public void anotarPersonaje(InscripcionRaid inscripcion, String usernameAuth) {
        // Validar que el personaje le pertenece al usuario del JWT
        if (!personajeRepository.perteneceAUsuario(inscripcion.getIdPersonaje(), usernameAuth)) {
            throw new RuntimeException("El personaje no pertenece a tu cuenta.");
        }

        // Validar que la Raid existe y está programada
        Raid raid = raidRepository.findById(inscripcion.getIdRaid());
        if (raid == null || !"PROGRAMADA".equals(raid.getEstado())) {
            throw new RuntimeException("La Raid no existe o ya no acepta inscripciones.");
        }

        // Ejecutar inserción (Aquí saltará el TRIGGER 1 si el item_level es bajo)
        inscripcionRepository.inscribir(inscripcion);
    }

    // Lógica para que el Guild Master confirme la asistencia (Req. 2)
    public void confirmarAsistencia(Integer idInscripcion, Integer idEjecutor, String usernameAuth) {
        // Validar que el ejecutor pertenece al GM
        if (!personajeRepository.perteneceAUsuario(idEjecutor, usernameAuth)) {
            throw new RuntimeException("No tienes permiso sobre el personaje ejecutor.");
        }

        // Validar que el ejecutor es realmente un Guild Master (en la tabla personaje o clanes)
        Personaje ejecutor = personajeRepository.findById(idEjecutor);
        if (!"Guild Master".equals(ejecutor.getRolClan())) {
            throw new RuntimeException("Solo el Guild Master puede confirmar asistencias.");
        }

        // Confirmar en la base de datos
        inscripcionRepository.confirmar(idInscripcion);
    }
}
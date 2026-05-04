package usach.cl.laboratorio1.service;

import usach.cl.laboratorio1.repository.InscripcionRepository;
import usach.cl.laboratorio1.repository.PersonajeRepository;
import usach.cl.laboratorio1.repository.RaidRepository;
import usach.cl.laboratorio1.tablas.InscripcionRaid;
import usach.cl.laboratorio1.tablas.Personaje;
import usach.cl.laboratorio1.tablas.Raid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private PersonajeRepository personajeRepository;

    @Autowired
    private RaidRepository raidRepository;

    // Inscribir un personaje a una raid (Req 2 + Req 5).
    // Validaciones en Java:
    // 1. El personaje debe pertenecer al usuario del JWT
    // 2. La raid debe existir y estar en estado PROGRAMADA
    // Validacion en PostgreSQL (automatica):
    // 3. El Trigger trg_validar_item_level verifica el nivel de equipo
    //    Si no cumple, PostgreSQL lanza excepcion y el INSERT se cancela.
    public void anotarPersonaje(InscripcionRaid inscripcion, String usernameAuth) {
        if (!personajeRepository.perteneceAUsuario(inscripcion.getIdPersonaje(), usernameAuth)) {
            throw new RuntimeException("El personaje no pertenece a tu cuenta.");
        }

        Raid raid = raidRepository.findById(inscripcion.getIdRaid());
        if (raid == null || !"PROGRAMADA".equals(raid.getEstado())) {
            throw new RuntimeException("La Raid no existe o ya no acepta inscripciones.");
        }

        // Validar que haya cupos disponibles para el rol seleccionado
        String rolEnRaid = inscripcion.getRolEnRaid();
        if (rolEnRaid != null) {
            int inscritos = inscripcionRepository.contarInscritosPorRol(
                    inscripcion.getIdRaid(), rolEnRaid);
            int cupoMaximo = switch (rolEnRaid) {
                case "TANQUE" -> raid.getTanques();
                case "HEALER" -> raid.getHealers();
                case "DPS" -> raid.getDps();
                default -> Integer.MAX_VALUE;
            };
            if (inscritos >= cupoMaximo) {
                throw new RuntimeException("No hay cupos disponibles para el rol " + rolEnRaid + ".");
            }
        }

        // Aqui se ejecuta el INSERT y el Trigger 1 actua automaticamente
        inscripcionRepository.inscribir(inscripcion);
    }

    // Confirmar asistencia de un jugador (Req 2).
    // Solo el Guild Master puede confirmar.
    // Validaciones:
    // 1. El ejecutor pertenece al usuario del JWT
    // 2. El ejecutor tiene rol "Guild Master"
    public void confirmarAsistencia(Integer idInscripcion, Integer idEjecutor,
                                    String usernameAuth) {
        if (!personajeRepository.perteneceAUsuario(idEjecutor, usernameAuth)) {
            throw new RuntimeException("No tienes permiso sobre el personaje ejecutor.");
        }

        Personaje ejecutor = personajeRepository.findById(idEjecutor);
        if (!"Guild Master".equals(ejecutor.getRolClan())) {
            throw new RuntimeException("Solo el Guild Master puede confirmar asistencias.");
        }

        inscripcionRepository.confirmar(idInscripcion);
    }
}
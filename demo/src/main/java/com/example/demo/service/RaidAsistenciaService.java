package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.PersonajeRepository;
import com.example.demo.repository.RaidAsistenciaRepository;
import com.example.demo.tablas.RaidAsistencia;

@Service
public class RaidAsistenciaService {

    @Autowired
    private RaidAsistenciaRepository raidAsistenciaRepository;

    @Autowired
    private PersonajeRepository personajeRepository;

    // Inscripción manual (TRIGGER 1)
    public void signUp(String usernameAuth, RaidAsistencia asistencia) {
        // Validar que el personaje que se quiere inscribir le pertenece al usuario
        if (!personajeRepository.isUsuarioPersonaje(asistencia.getIdPersonaje(), usernameAuth)) {
            throw new RuntimeException("No puedes inscribir a un personaje que no es tuyo.");
        }
        
        // El save ejecutará el INSERT. Si el Trigger de ilevel falla, 
        // se lanzará una DataAccessException que capturara el Controller. (Revisar logica)
        asistencia.setConfirmado("PENDIENTE");
        raidAsistenciaRepository.save(asistencia);
    }

    // Confirmar asistencia
    public void confirmAttendance(String usernameAuth, Integer idAsistencia) {
        RaidAsistencia asistencia = raidAsistenciaRepository.findById(idAsistencia);
        if (asistencia == null) throw new RuntimeException("Inscripción no encontrada.");

        // Validar propiedad
        if (!personajeRepository.isUsuarioPersonaje(asistencia.getIdPersonaje(), usernameAuth)) {
            throw new RuntimeException("Solo el dueño del personaje puede confirmar.");
        }

        asistencia.setConfirmado("CONFIRMADO");
        raidAsistenciaRepository.update(asistencia);
    }
}
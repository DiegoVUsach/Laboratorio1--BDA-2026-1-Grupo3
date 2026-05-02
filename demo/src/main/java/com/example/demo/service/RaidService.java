package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.PersonajeRepository;
import com.example.demo.repository.RaidRepository;

@Service
public class RaidService {

    @Autowired
    private RaidRepository raidRepository;

    @Autowired
    private PersonajeRepository personajeRepository;

    public void scheduleMassiveRaid(String usernameAuth, Integer idEjecutor, Integer idRaid, String fecha) {
        // Validar que el personaje pertenece al usuario del JWT
        if (!personajeRepository.isUsuarioPersonaje(idEjecutor, usernameAuth)) {
            throw new RuntimeException("No posees permisos sobre este personaje.");
        }

        // Validar que el personaje es Guild Master (Lider en tabla CLANES)
        if (!personajeRepository.isGuildMaster(idEjecutor)) {
            throw new RuntimeException("Solo el Guild Master puede crear eventos masivos.");
        }

        // Obtener el ID del clan del líder para invitar a sus miembros
        Integer idClan = personajeRepository.findById(idEjecutor).getIdClan();

        // Llamar al procedimiento en la BD (Stored Procedure)
        raidRepository.createMassiveEvent(idClan, idRaid, fecha);
    }
}
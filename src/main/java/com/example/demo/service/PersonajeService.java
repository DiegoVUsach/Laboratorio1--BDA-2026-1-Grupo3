package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.PersonajeRepository;
import com.example.demo.tablas.Personaje;

@Service
public class PersonajeService {

    @Autowired
    private PersonajeRepository personajeRepository;

    public void asignarNuevoRol(String usernameAuth, Integer idEjecutor, Integer idObjetivo, String nuevoRol) {
        // El personaje ejecutor debe pertenecer al usuario logueado (Seguridad JWT)
        if (!personajeRepository.perteneceAUsuario(idEjecutor, usernameAuth)) {
            throw new RuntimeException("No tienes permiso sobre el personaje ejecutor.");
        }

        Personaje ejecutor = personajeRepository.findById(idEjecutor);
        Personaje objetivo = personajeRepository.findById(idObjetivo);

        // Validar que el ejecutor es el LIDER del clan (Guild Master)
        if (!personajeRepository.esLiderDeClan(idEjecutor, ejecutor.getIdClan())) {
            throw new RuntimeException("Solo el Guild Master puede cambiar rangos.");
        }

        // Validar que están en el mismo clan
        if (objetivo == null || !objetivo.getIdClan().equals(ejecutor.getIdClan())) {
            throw new RuntimeException("El objetivo no pertenece a tu clan.");
        }

        // No permitir auto-asignarse o crear otro Guild Master por esta via
        if (nuevoRol.equalsIgnoreCase("Guild Master")) {
            throw new RuntimeException("Para transferir el liderazgo usa la opción de Transferir.");
        }

        personajeRepository.updateRol(idObjetivo, nuevoRol);
    }
}
package com.example.demo.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ClanesRepository;
import com.example.demo.repository.PersonajeRepository;
import com.example.demo.tablas.Clanes;
import com.example.demo.tablas.Personaje;

@Service
public class ClanesService {

    @Autowired
    private ClanesRepository clanesRepository;
    @Autowired
    private PersonajeRepository personajeRepository;

    public List<Clanes> findAll() {
        return clanesRepository.findAll();
    }

    public Clanes findById(Integer id) {
        return clanesRepository.findById(id);
    }

    public void save(Clanes clan) {
        clanesRepository.save(clan);
    }

    public void transferLeadership(String usernameAuth, Integer idClan, Integer idCurrentLeader, Integer idNewLeader) { 
        Clanes clan = clanesRepository.findById(idClan);
        if (clan == null) throw new RuntimeException("Clan no encontrado.");

        // Validar que el ejecutor es el lider registrado
        if (!clan.getIdLider().equals(idCurrentLeader)) {
            throw new RuntimeException("El personaje ejecutor no es el lider actual.");
        }

        // Validar propiedad del personaje (JWT)
        if (!personajeRepository.perteneceAUsuario(idCurrentLeader, usernameAuth)) {
            throw new RuntimeException("No tienes permiso sobre este personaje.");
        }

        // Validar que el nuevo lider es del mismo clan
        Personaje newLeaderObj = personajeRepository.findById(idNewLeader);
        if (newLeaderObj == null || !newLeaderObj.getIdClan().equals(idClan)) {
            throw new RuntimeException("El nuevo lider debe pertenecer al mismo clan.");
        }

        // EJECUCIÓN (Esto dispara el TRIGGER 2 en la BD)
        clan.setIdLider(idNewLeader);
        clanesRepository.update(clan); 

        // Ajustar roles
        personajeRepository.updateRol(idCurrentLeader, "Member");
        personajeRepository.updateRol(idNewLeader, "Guild Master");
    }
}
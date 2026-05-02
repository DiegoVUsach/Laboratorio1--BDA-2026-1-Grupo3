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
        // Obtener el clan
        Clanes clan = clanesRepository.findById(idClan);
        if (clan == null) throw new RuntimeException("Clan no encontrado.");

        // SEGURIDAD: El personaje que intenta transferir es realmente el lider actual?
        if (!clan.getIdLider().equals(idCurrentLeader)) {
            throw new RuntimeException("El personaje ejecutor no es el líder de este clan.");
        }

        // SEGURIDAD: Ese lider actual le pertenece al usuario que inicio sesion (JWT)?
        if (!personajeRepository.isUsuarioPersonaje(idCurrentLeader, usernameAuth)) {
            throw new RuntimeException("No tienes permiso para mandar órdenes desde este personaje.");
        }

        // LÓGICA: El nuevo lider pertenece al mismo clan?
        Personaje newLeaderObj = personajeRepository.findById(idNewLeader);
        if (newLeaderObj == null || !newLeaderObj.getIdClan().equals(idClan)) {
            throw new RuntimeException("El nuevo líder debe pertenecer al mismo clan.");
        }

        // EJECUCION
        clan.setIdLider(idNewLeader);
        clanesRepository.update(clan); 
        
        // el Trigger 2 (Auditoría) en PostgreSQL 
        // debe capturar el cambio
    }
}
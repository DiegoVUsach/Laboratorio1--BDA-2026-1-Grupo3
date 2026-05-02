package com.example.demo.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.PersonajeRepository;
import com.example.demo.repository.RolesRepository;
import com.example.demo.tablas.Personaje;
import com.example.demo.tablas.Roles;

@Service
public class PersonajeService {
    @Autowired
    private PersonajeRepository personajeRepository;
    @Autowired
    private RolesRepository rolesRepository;

    public Personaje findById(Integer id) {
        return personajeRepository.findById(id);
    }

    public List<Personaje> findAll() {
        return personajeRepository.findAll();
    }

    public int save(Personaje entity) {
        return personajeRepository.save(entity);
    }


    public void newRol(String usernameAuth, Integer idEjecutor, Integer idObjetivo, Integer idNuevoRol) {

        Roles roleGuildMaster = rolesRepository.findByName("Guild Master");
        
        if (roleGuildMaster != null && idNuevoRol.equals(roleGuildMaster.getIdRol())) {
            throw new RuntimeException("No se puede asignar el rol de Guild Master por este medio.");
        }

        // Validar que el ejecutor pertenece al usuario del JWT
        if (!personajeRepository.isUsuarioPersonaje(idEjecutor, usernameAuth)) {
            throw new RuntimeException("El personaje ejecutor no pertenece a tu cuenta.");
        }

        // Validar que el ejecutor es el LIDER real en la tabla CLANES
        if (!personajeRepository.isGuildMaster(idEjecutor)) {
            throw new RuntimeException("Solo el líder del clan puede asignar rangos.");
        }

        // Validar que ambos personajes pertenecen al mismo clan
        Personaje objetivo = personajeRepository.findById(idObjetivo);
        Personaje ejecutor = personajeRepository.findById(idEjecutor);

        if (objetivo == null || !objetivo.getIdClan().equals(ejecutor.getIdClan())) {
            throw new RuntimeException("El personaje objetivo no pertenece a tu clan.");
        }

        // Si todo está ok, actualizamos el rol (Raider o Miembro)
        objetivo.setIdRol(idNuevoRol);
        personajeRepository.update(objetivo);
    }
}
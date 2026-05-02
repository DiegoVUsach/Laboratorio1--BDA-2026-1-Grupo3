package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.repository.AuditoriaLiderazgoRepository;
import com.example.demo.tablas.AuditoriaLiderazgo;
import java.util.List;

@Service
public class AuditoriaLiderazgoService {

    @Autowired
    private AuditoriaLiderazgoRepository auditoriaRepository;

    // Método para obtener el historial
    public List<AuditoriaLiderazgo> obtenerHistorialAuditoria() {
        return auditoriaRepository.findAll();
    }

}
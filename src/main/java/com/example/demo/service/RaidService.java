package com.example.demo.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.RaidDTO;
import com.example.demo.repository.InscripcionRepository;
import com.example.demo.repository.RaidRepository;
import com.example.demo.tablas.Raid;

@Service
public class RaidService {
    @Autowired
    private RaidRepository raidRepository;
    @Autowired
    private InscripcionRepository inscripcionRepository;

    public List<RaidDTO> getCalendarioSemanal() {
        List<Raid> raids = raidRepository.findAll();
        return raids.stream().map(r -> {
            int t = r.getTanques() - inscripcionRepository.contarInscritosPorRol(r.getIdRaid(), "TANQUE");
            int h = r.getHealers() - inscripcionRepository.contarInscritosPorRol(r.getIdRaid(), "HEALER");
            int d = r.getDps() - inscripcionRepository.contarInscritosPorRol(r.getIdRaid(), "DPS");
            return new RaidDTO(r, t, h, d);
        }).collect(Collectors.toList());
    }
}
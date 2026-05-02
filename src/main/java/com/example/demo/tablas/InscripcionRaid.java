package com.example.demo.tablas;
import lombok.Data;

@Data
public class InscripcionRaid {
    private Integer idInscripcion;
    private Integer idRaid;
    private Integer idPersonaje;
    private String rolEnRaid; // TANQUE, HEALER, DPS
    private Boolean confirmado;
}
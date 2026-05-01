package com.example.demo.tablas;
import lombok.Data;

@Data
public class RaidAsistencia {
    private Integer idAsistencia;
    private Integer idEvento;
    private Integer idPersonaje;
    private String rolCombateAsignado;
    private String confirmado;
}
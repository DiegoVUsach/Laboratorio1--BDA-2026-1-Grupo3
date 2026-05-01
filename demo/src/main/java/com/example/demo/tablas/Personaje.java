package com.example.demo.tablas;
import lombok.Data;

@Data
public class Personaje {
    private Integer idPersonaje;
    private Integer idUsuario;
    private Integer idClan;
    private Integer idClase;
    private Integer idRol;
    private String nombrePersonaje;
    private Integer nivel;
    private Integer itemLevel;
    private Integer puntosDkpActuales;
}
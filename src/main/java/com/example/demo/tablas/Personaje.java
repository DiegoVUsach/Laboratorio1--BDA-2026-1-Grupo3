package com.example.demo.tablas;
import lombok.Data;

@Data
public class Personaje {
    private Integer idPersonaje;
    private Integer idUsuario; // Para saber a qué cuenta pertenece
    private Integer idClan;    // Para el SP de invitación masiva y ranking
    private String nombrePersonaje;
    private String clase;      // "Guerrero", "Mago", etc.
    private String rolClan;    // "Guild Master", "Raider", "Member"
    private Integer nivel;
    private Integer itemLevel;
    private Integer puntosDkpActuales;
}
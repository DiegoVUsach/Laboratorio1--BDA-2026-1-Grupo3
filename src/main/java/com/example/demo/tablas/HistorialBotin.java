package com.example.demo.tablas;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HistorialBotin {
    private Integer idEntrega;
    private Integer idPersonaje;
    private Integer idItem;
    private Integer idRaid;
    private LocalDateTime fechaEntrega;
    // Campos extra para que el usuario entienda qué ve
    private String nombreItem; 
    private String nombrePersonaje;
}
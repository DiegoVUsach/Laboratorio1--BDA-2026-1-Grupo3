package com.example.demo.tablas;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Raid {
    private Integer idRaid;
    private String nombreRaid;
    private LocalDateTime fechaRaid;
    private Integer itemLevelMinimo;
    private Integer Tanques;
    private Integer Healers;
    private Integer Dps;
    private String estado; // PROGRAMADA, FINALIZADA
}
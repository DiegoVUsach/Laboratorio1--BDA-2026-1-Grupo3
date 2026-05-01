package com.example.demo.tablas;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Evento {
    private Integer idEvento;
    private Integer idRaid;
    private Integer idClan;
    private String nombreEvento;
    private String estado;
    private LocalDateTime fechaHora;
}
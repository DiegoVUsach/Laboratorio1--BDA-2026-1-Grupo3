package com.example.demo.tablas;
import java.time.LocalDate;

import lombok.Data;

@Data
public class AuditoriaLiderazgo {
    private Integer idAudLider;
    private Integer idClan;
    private Integer idLiderAnterior;
    private Integer idLiderNuevo;
    private LocalDate fechaCambio;
}
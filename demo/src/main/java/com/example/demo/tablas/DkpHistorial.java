package com.example.demo.tablas;
import java.time.LocalDate;

import lombok.Data;

@Data
public class DkpHistorial {
    private Integer idLog;
    private Integer idPersonaje;
    private Integer cantidad;
    private String motivo;
    private LocalDate fecha;
}
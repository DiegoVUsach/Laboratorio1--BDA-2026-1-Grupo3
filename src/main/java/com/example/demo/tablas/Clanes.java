package com.example.demo.tablas;
import lombok.Data;

@Data
public class Clanes {
    private Integer idClan;
    private Integer idLider; // ID del GM del clan
    private String nombreClan;
}
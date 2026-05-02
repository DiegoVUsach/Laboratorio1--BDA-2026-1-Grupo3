package com.example.demo.dto;
import com.example.demo.tablas.Raid;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RaidDTO {
    private Raid raid;
    private int cuposTanqueLibres; // Lo que queda disponible
    private int cuposHealerLibres;
    private int cuposDpsLibres;
}
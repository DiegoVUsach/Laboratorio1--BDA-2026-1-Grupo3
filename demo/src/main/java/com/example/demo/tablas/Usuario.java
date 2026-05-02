package com.example.demo.tablas;
import lombok.Data;

@Data
public class Usuario {
    private Integer idUsuario;
    private String nombreUsuario;
    private String password; 
}
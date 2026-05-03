package usach.cl.laboratorio1.tablas;

import lombok.Data;

@Data
public class Usuario {
    private Integer idUsuario;
    private String nombreUsuario;
    private String password;
    private String rol;
}
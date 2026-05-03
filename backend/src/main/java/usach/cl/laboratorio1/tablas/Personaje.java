package usach.cl.laboratorio1.tablas;

import lombok.Data;

@Data
public class Personaje {
    private Integer idPersonaje;
    private Integer idUsuario;
    private Integer idClan;
    private String nombrePersonaje;
    private String clase;
    private String rolClan;
    private Integer nivel;
    private Integer itemLevel;
    private Integer puntosDkpActuales;
}
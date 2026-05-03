package usach.cl.laboratorio1.tablas;

import lombok.Data;

@Data
public class InscripcionRaid {
    private Integer idInscripcion;
    private Integer idRaid;
    private Integer idPersonaje;
    private String rolEnRaid;
    private Boolean confirmado;
}
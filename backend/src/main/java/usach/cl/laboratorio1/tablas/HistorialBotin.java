package usach.cl.laboratorio1.tablas;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HistorialBotin {
    private Integer idEntrega;
    private Integer idPersonaje;
    private Integer idItem;
    private Integer idRaid;
    private LocalDateTime fechaEntrega;
    private String nombreItem;
    private String nombrePersonaje;
}
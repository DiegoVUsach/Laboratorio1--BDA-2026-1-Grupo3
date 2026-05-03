package usach.cl.laboratorio1.tablas;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Raid {
    private Integer idRaid;
    private String nombreRaid;
    private LocalDateTime fechaRaid;
    private Integer itemLevelMinimo;
    private Integer tanques;
    private Integer healers;
    private Integer dps;
    private String estado;
}
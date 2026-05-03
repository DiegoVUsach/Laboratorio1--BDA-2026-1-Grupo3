package usach.cl.laboratorio1.tablas;

import lombok.Data;

@Data
public class Item {
    private Integer idItem;
    private String nombreItem;
    private String rareza;
    private Integer costoDkp;
}
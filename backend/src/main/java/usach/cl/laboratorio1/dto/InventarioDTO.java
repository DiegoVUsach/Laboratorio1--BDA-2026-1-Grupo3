package usach.cl.laboratorio1.dto;

import lombok.Data;

@Data
public class InventarioDTO {
    private Integer idInventario;
    private Integer idPersonaje;
    private Integer armaduraEquipado;
    private Integer armaEquipado;
    private Integer accesorioEquipado;
    private String nombreArmadura;
    private String nombreArma;
    private String nombreAccesorio;
}

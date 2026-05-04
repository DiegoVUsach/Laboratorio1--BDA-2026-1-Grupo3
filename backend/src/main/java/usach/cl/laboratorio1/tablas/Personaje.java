package usach.cl.laboratorio1.tablas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@Data
public class Personaje {
    private Integer idPersonaje;
    private Integer idUsuario;
    private Integer idClan;
    private String nombrePersonaje;
    private String clase;
    private Integer nivel;
    private Faccion faccion;
    private String rolClan;
    private Integer itemLevel;
    private Integer puntosDkpActuales;

    public enum Faccion {
        PRIMORDIALES_LUZ("Los Primordiales de la Luz"),
        HIJOS_GRIS("Los Hijos del Gris"),
        MARCADOS_ABISMO("Los Marcados por el Abismo");

        private final String label;

        Faccion(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static Faccion fromLabel(String label) {
            if (label == null) {
                return null;
            }
            for (Faccion faccion : values()) {
                if (faccion.label.equals(label)) {
                    return faccion;
                }
            }
            throw new IllegalArgumentException("Faccion invalida.");
        }
    }
}
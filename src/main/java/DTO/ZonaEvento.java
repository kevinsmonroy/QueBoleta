package DTO;
import java.io.Serializable;

import lombok.Data;

@Data
public class ZonaEvento implements Serializable {
    private String nombreZona;
    private double precio;
    private int capacidadTotal;
    private int boletasDisponibles;
}
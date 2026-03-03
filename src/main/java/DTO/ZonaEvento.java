package DTO;

import lombok.Data;

@Data
public class ZonaEvento {
    private String nombreZona;
    private double precio;
    private int capacidadTotal;
    private int boletasDisponibles;
}
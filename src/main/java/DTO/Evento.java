package DTO;

import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Evento {
    private String nombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String lugar;
    private String patrocinador;
    private List<ZonaEvento> zonas;
}
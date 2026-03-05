package DTO;
import java.io.Serializable;

import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Evento implements Serializable {
    private String nombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String lugar;
    private String patrocinador;
    private List<ZonaEvento> zonas;
}
package DTO;
import java.io.Serializable;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Venta implements java.io.Serializable {
    private String idVenta;
    private Usuario cliente;
    private Evento evento;
    private EstadoVenta estado;
    private LocalDateTime fechaHoraReserva;
    private int cantidadReservada;
    private String nombreZona;
}
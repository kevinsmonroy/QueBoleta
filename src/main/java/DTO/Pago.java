package DTO;

import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Pago {
    private int  idPago;
    private MetodoPago metodo;
    private double valor;
    private DateTimeFormatter fechaPago;
    private boolean aprobado;



}

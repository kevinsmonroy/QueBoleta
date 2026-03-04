package DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DetalleVenta {

    private int idDetalle;
    private Localidad localidad;
    private int cantidad;
    private Double precioUnitario;

}

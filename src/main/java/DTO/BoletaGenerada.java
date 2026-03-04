package DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BoletaGenerada {
    private int idBoleta;
    private  String codigo;
    private Localidad localidad;
}

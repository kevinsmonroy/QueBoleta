package DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Usuario {
    private int  numeroIdentificacion;
    private String nombre;
    private String correo;
    private String telefono;
}

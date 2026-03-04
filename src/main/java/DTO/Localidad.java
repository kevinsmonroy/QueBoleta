package DTO;

import lombok.Getter;

@Getter
public enum Localidad {
    ZONA_A(200000), ZONA_B(100000), ZONA_C(50000);

    private final double precio;

    Localidad(double precio) {
        this.precio = precio;
    }
}
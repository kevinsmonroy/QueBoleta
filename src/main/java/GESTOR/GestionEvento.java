package GESTOR;

import DTO.Evento;
import DTO.ZonaEvento;
import java.util.ArrayList;
import java.util.List;

public class GestionEvento {
    private List<Evento> eventos = new ArrayList<>();

    public String registrarEvento(Evento nuevoEvento) {
        int totalBoletas = 0;

        // RF-01: Validar máximo 10.000 boletas
        if (nuevoEvento.getZonas() != null) {
            for (ZonaEvento zona : nuevoEvento.getZonas()) {
                totalBoletas += zona.getCapacidadTotal();
            }
        }

        if (totalBoletas > 10000) {
            return "Error: La capacidad total no puede superar las 10,000 boletas.";
        }

        eventos.add(nuevoEvento);
        return "Evento '" + nuevoEvento.getNombre() + "' registrado con éxito.";
    }
}
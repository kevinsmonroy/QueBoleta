package GESTOR;

import DTO.*;
import PERSISTENCIA.DataStorage;
import java.util.UUID;
import java.time.LocalDateTime;

public class GestionVenta {

    public String procesarReserva(Usuario cliente, Evento evento, String nombreZona, int cantidad) {
        // Máximo 10 boletas
        if (cantidad > 10) {
            return "ERROR: No se permiten más de 10 boletas por cliente.";
        }

        // Buscamos la zona en el evento
        ZonaEvento zona = evento.getZonas().stream()
                .filter(z -> z.getNombreZona().equalsIgnoreCase(nombreZona))
                .findFirst().orElse(null);

        if (zona == null || zona.getBoletasDisponibles() < cantidad) {
            return "ERROR: No hay disponibilidad en la zona seleccionada.";
        }

        // crea reserva por ahora en memoria
        Venta reserva = new Venta();
        reserva.setIdVenta(UUID.randomUUID().toString());
        reserva.setCliente(cliente);
        reserva.setEvento(evento);
        reserva.setEstado(EstadoVenta.RESERVADA);
        reserva.setFechaHoraReserva(LocalDateTime.now()); // Para el RNF-01

        zona.setBoletasDisponibles(zona.getBoletasDisponibles() - cantidad);
        DataStorage.VENTAS.add(reserva);

        return "EXITO: Reserva generada con ID: " + reserva.getIdVenta();
    }
}
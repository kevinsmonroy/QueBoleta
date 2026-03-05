package GESTOR;

import DTO.*;
import PERSISTENCIA.DataStorage;
import java.util.UUID;
import java.time.LocalDateTime;

public class GestionVenta {

    public String procesarReserva(Usuario cliente, Evento evento, String nombreZona, int cantidad) {
        if (cantidad > 10) {
            return "ERROR: No se permiten más de 10 boletas por cliente.";
        }

        if (!nombreZona.equalsIgnoreCase("A") &&
                !nombreZona.equalsIgnoreCase("B") &&
                !nombreZona.equalsIgnoreCase("C")) {
            return "ERROR: Zona inválida. Solo se permite reservar en A, B o C.";
        }

        ZonaEvento zona = evento.getZonas().stream()
                .filter(z -> z.getNombreZona().equalsIgnoreCase(nombreZona))
                .findFirst().orElse(null);

        if (zona == null) {
            return "ERROR: La zona " + nombreZona + " no existe en este evento.";
        }

        if (zona.getBoletasDisponibles() < cantidad) {
            return "ERROR: No hay disponibilidad suficiente en la zona " + nombreZona + ".";
        }

        Venta reserva = new Venta();
        reserva.setIdVenta(UUID.randomUUID().toString());
        reserva.setCliente(cliente);
        reserva.setEvento(evento);
        reserva.setEstado(EstadoVenta.RESERVADA);
        reserva.setFechaHoraReserva(LocalDateTime.now());

        reserva.setNombreZona(nombreZona);
        reserva.setCantidadReservada(cantidad);

        zona.setBoletasDisponibles(zona.getBoletasDisponibles() - cantidad);
        DataStorage.VENTAS.add(reserva);

        return "EXITO: Reserva generada con ID: " + reserva.getIdVenta();
    }
}
package GESTOR;

import DTO.EstadoVenta;
import DTO.Venta;
import DTO.ZonaEvento;
import PERSISTENCIA.DataStorage;
import UTIL.Configuracion;
import java.time.LocalDateTime;

public class GestionExpiracion {
    private Configuracion config = new Configuracion();

    public void procesarExpiraciones() {
        // Leemos el tiempo de expiración desde el properties (RNF-01)
        int horasLimite = config.getTiempoReservaHoras();
        LocalDateTime ahora = LocalDateTime.now();

        for (Venta reserva : DataStorage.VENTAS) {
            // Solo procesamos las que estan en estado RESERVADA
            if (reserva.getEstado() == EstadoVenta.RESERVADA) {

                // Validamos tiempo
                if (reserva.getFechaHoraReserva().plusHours(horasLimite).isBefore(ahora)) {

                    // 1. Cambiamos el estado a CANCELADA
                    reserva.setEstado(EstadoVenta.CANCELADA);

                    // 2. Devolvemos las boletas al inventario del evento
                    System.out.println("Reserva " + reserva.getIdVenta() + " ha expirado y fue cancelada.");
                }
            }
        }
    }
}
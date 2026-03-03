package GESTOR;

import DTO.EstadoVenta;
import DTO.Venta;
import PERSISTENCIA.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class GestionExpiracionTest {
    private GestionExpiracion gestorExpiracion;

    @BeforeEach
    void setUp() {
        gestorExpiracion = new GestionExpiracion();
        DataStorage.VENTAS.clear();
    }

    @Test
    void testProcesarExpiracionExitosa() {
        // set reserva en 25h
        Venta reservaVieja = new Venta();
        reservaVieja.setIdVenta("EXP-001");
        reservaVieja.setEstado(EstadoVenta.RESERVADA);
        reservaVieja.setFechaHoraReserva(LocalDateTime.now().minusHours(25));

        DataStorage.VENTAS.add(reservaVieja);

        gestorExpiracion.procesarExpiraciones();

        // validamos que se cancele
        assertEquals(EstadoVenta.CANCELADA, reservaVieja.getEstado(),
                "La reserva debería haber cambiado a CANCELADA por tiempo expirado");
    }

    @Test
    void testNoExpirarReservasRecientes() {
        // set nueva reserva hace 10m
        Venta reservaNueva = new Venta();
        reservaNueva.setIdVenta("NEW-001");
        reservaNueva.setEstado(EstadoVenta.RESERVADA);
        reservaNueva.setFechaHoraReserva(LocalDateTime.now().minusMinutes(10));

        DataStorage.VENTAS.add(reservaNueva);

        gestorExpiracion.procesarExpiraciones();

        // verificamos
        assertEquals(EstadoVenta.RESERVADA, reservaNueva.getEstado(),
                "La reserva reciente NO debería haber expirado");
    }
}
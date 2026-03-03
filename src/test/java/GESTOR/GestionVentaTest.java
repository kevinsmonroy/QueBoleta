package GESTOR;

import DTO.*;
import PERSISTENCIA.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GestionVentaTest {
    private GestionVenta gestionVenta;
    private Evento eventoPrueba;
    private Usuario cliente;

    @BeforeEach
    void setUp() {
        gestionVenta = new GestionVenta();
        cliente = new Usuario();
        cliente.setCedula("12345");

        eventoPrueba = new Evento();
        eventoPrueba.setNombre("Concierto Rock");

        ZonaEvento zona = new ZonaEvento();
        zona.setNombreZona("VIP");
        zona.setCapacidadTotal(100);
        zona.setBoletasDisponibles(100);

        List<ZonaEvento> zonas = new ArrayList<>();
        zonas.add(zona);
        eventoPrueba.setZonas(zonas);
    }

    @Test
    void testValidarLimiteDiezBoletas() {
        // Intentamos reservar 11 boletas (debe fallar según RF-03)
        String resultado = gestionVenta.procesarReserva(cliente, eventoPrueba, "VIP", 11);

        assertEquals("ERROR: No se permiten más de 10 boletas por cliente.", resultado);
        System.out.println("Test de límite superado: PASADO");
    }

    @Test
    void testReservaExitosa() {
        // Intentamos reservar 5 boletas (debe funcionar)
        String resultado = gestionVenta.procesarReserva(cliente, eventoPrueba, "VIP", 5);

        assertTrue(resultado.contains("EXITO"));
        assertEquals(95, eventoPrueba.getZonas().get(0).getBoletasDisponibles());
        System.out.println("Test de reserva exitosa: PASADO");
    }
}
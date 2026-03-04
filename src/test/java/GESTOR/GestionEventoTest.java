package GESTOR;

import DTO.Evento;
import DTO.ZonaEvento;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class GestionEventoTest {

    @Test
    public void testValidarExcesoBoletas() {
        GestionEvento gestor = new GestionEvento();
        Evento eventoCargado = new Evento();

        // Creamos una zona que excede el límite (11,000 boletas)
        ZonaEvento zonaExcesiva = new ZonaEvento();
        zonaExcesiva.setCapacidadTotal(11000);

        eventoCargado.setZonas(Arrays.asList(zonaExcesiva));

        String resultado = gestor.registrarEvento(eventoCargado);

        // Verificamos que el sistema devuelva el error y no lo registre
        assertTrue(resultado.contains("Error"), "Debería fallar por superar las 10,000 boletas");
    }
}
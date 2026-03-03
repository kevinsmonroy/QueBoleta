package UTIL;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionTest {
    @Test
    public void testGetTiempoReservaHoras() {
        int horas = Configuracion.getTiempoReservaHoras();
        // Verificamos que cargue las 24 horas definidas
        assertEquals(24, horas, "El tiempo de reserva debería ser 24 horas.");
    }
}
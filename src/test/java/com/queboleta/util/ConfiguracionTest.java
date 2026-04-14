package com.queboleta.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionTest {
    @Test
    public void testGetTiempoReservaHoras() {
        Configuracion config = new Configuracion();

        int horas = config.getTiempoReservaHoras();

        assertEquals(24, horas, "El tiempo de reserva debería ser 24 horas.");
    }
}
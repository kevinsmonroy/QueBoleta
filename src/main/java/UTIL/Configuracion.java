package UTIL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuracion {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Configuracion.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Lo siento, no se pudo encontrar config.properties. Usando valores por defecto.");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int getTiempoReservaHoras() {
        // Retorna el valor del archivo o 24 por defecto si hay error [cite: 78, 120]
        return Integer.parseInt(properties.getProperty("reserva.expiracion.horas", "24"));
    }
}
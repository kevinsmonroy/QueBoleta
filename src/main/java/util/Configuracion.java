package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuracion {
    private Properties properties = new Properties();

    public Configuracion() {
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            // Si falla, al menos tendremos un rastro en consola
            System.err.println("No se pudo cargar config.properties, usando valores por defecto.");
        }
    }

    /**
     * RNF-01: Lee las horas de reserva desde el archivo externo.
     */
    public int getTiempoReservaHoras() {
        // Buscamos la propiedad 'tiempoReserva'. Si no existe, devuelve "24" por defecto.
        String valor = properties.getProperty("tiempoReserva", "24");
        return Integer.parseInt(valor);
    }
}
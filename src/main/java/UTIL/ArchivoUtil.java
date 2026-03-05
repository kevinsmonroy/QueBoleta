package UTIL;

import PERSISTENCIA.DataStorage;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class ArchivoUtil {
    private static final String FILE_NAME = "datos_queboleta.dat";

    public static void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(new ArrayList<>(DataStorage.EVENTOS));
            oos.writeObject(new ArrayList<>(DataStorage.VENTAS));
            System.out.println("Datos guardados localmente.");
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void cargarDatos() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            DataStorage.EVENTOS = (List<DTO.Evento>) ois.readObject();
            DataStorage.VENTAS = (List<DTO.Venta>) ois.readObject();
            System.out.println("Datos cargados desde el archivo.");
        } catch (Exception e) {
            System.err.println("No se pudieron cargar los datos previos.");
        }
    }
}
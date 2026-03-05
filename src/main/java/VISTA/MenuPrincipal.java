package VISTA;

import DTO.*;
import GESTOR.*;
import PERSISTENCIA.DataStorage;
import UTIL.ArchivoUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {
    private Scanner sc = new Scanner(System.in);
    private GestionVenta gestorVenta = new GestionVenta();
    private GestionEvento gestorEvento = new GestionEvento();

    public void iniciar() {
        ArchivoUtil.cargarDatos();

        int opcion = 0;
        do {
            System.out.println("\n========================================");
            System.out.println("   SISTEMA QUEBOLETA! - MODO CONSOLA");
            System.out.println("========================================");
            System.out.println("1. Registrar Evento (Admin)");
            System.out.println("2. Realizar Reserva (Cliente)");
            System.out.println("3. Ver Cartelera Actual");
            System.out.println("4. Mis Reservas y Pagar (Cliente)");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
                switch (opcion) {
                    case 1 -> registrarEvento();
                    case 2 -> realizarReserva();
                    case 3 -> verCartelera();
                    case 4 -> gestionarMisReservas();
                    case 5 -> System.out.println("Saliendo del sistema...");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: Ingrese un número válido.");
                opcion = 0;
            }
        } while (opcion != 5);
    }

    private void registrarEvento() {
        System.out.println("\n--- REGISTRAR NUEVO EVENTO ---");
        Evento e = new Evento();
        System.out.print("Nombre del Evento: ");
        e.setNombre(sc.nextLine());

        List<ZonaEvento> zonas = new ArrayList<>();
        boolean añadirMas = true;
        while (añadirMas) {
            System.out.print("Nombre de la localidad (ej: VIP, General): ");
            String nZona = sc.nextLine();
            System.out.print("Capacidad total de esta zona: ");
            int cap = Integer.parseInt(sc.nextLine());

            ZonaEvento z = new ZonaEvento();
            z.setNombreZona(nZona);
            z.setCapacidadTotal(cap);
            z.setBoletasDisponibles(cap);
            zonas.add(z);

            System.out.print("¿Desea añadir otra zona? (s/n): ");
            if (sc.nextLine().equalsIgnoreCase("n")) añadirMas = false;
        }
        e.setZonas(zonas);

        String res = gestorEvento.registrarEvento(e);
        if (res.contains("éxito") || res.contains("exitoso")) {
            DataStorage.EVENTOS.add(e);
            ArchivoUtil.guardarDatos();
            System.out.println("✅ " + res);
        }
    }

    private void verCartelera() {
        System.out.println("\n--- CARTELERA DE EVENTOS ---");
        if (DataStorage.EVENTOS.isEmpty()) {
            System.out.println("No hay eventos registrados.");
            return;
        }
        for (int i = 0; i < DataStorage.EVENTOS.size(); i++) {
            Evento e = DataStorage.EVENTOS.get(i);
            System.out.print((i + 1) + ". " + e.getNombre() + " | Localidades: ");
            for (ZonaEvento z : e.getZonas()) {
                System.out.print("[" + z.getNombreZona() + ": " + z.getBoletasDisponibles() + "] ");
            }
            System.out.println();
        }
    }

    private void realizarReserva() {
        if (DataStorage.EVENTOS.isEmpty()) {
            System.out.println("No hay eventos disponibles para reservar.");
            return;
        }
        verCartelera();
        System.out.print("Seleccione el número del evento: ");
        int numEv = Integer.parseInt(sc.nextLine()) - 1;
        Evento evSel = DataStorage.EVENTOS.get(numEv);

        System.out.println("Zonas disponibles: ");
        for (ZonaEvento z : evSel.getZonas()) System.out.println("- " + z.getNombreZona());
        System.out.print("Escriba el nombre de la zona: ");
        String zonaNom = sc.nextLine();

        System.out.print("Nombre Cliente: ");
        String nomCli = sc.nextLine();
        System.out.print("ID / Cédula: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Cantidad de boletas (Máx 10): ");
        int cant = Integer.parseInt(sc.nextLine());

        Usuario u = new Usuario();
        u.setNombre(nomCli);
        u.setNumeroIdentificacion(id);

        String resultado = gestorVenta.procesarReserva(u, evSel, zonaNom, cant);
        System.out.println(resultado);

        if (resultado.contains("EXITO")) {
            ArchivoUtil.guardarDatos();
        }
    }

    private void gestionarMisReservas() {
        System.out.print("\nIngrese su ID para consultar: ");
        int idBusca = Integer.parseInt(sc.nextLine());
        boolean encontro = false;

        for (Venta v : DataStorage.VENTAS) {
            if (v.getCliente().getNumeroIdentificacion() == idBusca) {
                encontro = true;
                System.out.println("\n------------------------------");
                System.out.println("Reserva ID: " + v.getIdVenta().substring(0, 8));
                System.out.println("Evento: " + v.getEvento().getNombre());
                System.out.println("Zona: " + v.getNombreZona());
                System.out.println("Cantidad: " + v.getCantidadReservada());
                System.out.println("Estado Actual: " + v.getEstado());

                if (v.getEstado() == EstadoVenta.RESERVADA) {
                    System.out.print("¿Desea PAGAR esta reserva ahora? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        v.setEstado(EstadoVenta.PAGADA);
                        System.out.println("✅ Pago procesado exitosamente.");
                        ArchivoUtil.guardarDatos();
                    }
                }
            }
        }
        if (!encontro) System.out.println("No se encontraron reservas para el ID: " + idBusca);
    }
}
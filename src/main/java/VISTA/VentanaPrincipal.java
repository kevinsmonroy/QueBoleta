package VISTA;

import DTO.*;
import GESTOR.*;
import PERSISTENCIA.DataStorage;
import UTIL.ArchivoUtil;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private GestionEvento gestorEvento = new GestionEvento();
    private GestionVenta gestorVenta = new GestionVenta();
    private DefaultListModel<String> modeloLista = new DefaultListModel<>();
    private JList<String> listaEventos = new JList<>(modeloLista);

    public VentanaPrincipal() {
        setTitle("QueBoleta! - Sistema de Gestión");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ArchivoUtil.cargarDatos();

        // --- ENCABEZADO ---
        JLabel titulo = new JLabel("🎟️ SISTEMA DE RESERVAS QUEBOLETA!", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // --- LISTA CENTRAL ---
        actualizarLista();
        JScrollPane scroll = new JScrollPane(listaEventos);
        scroll.setBorder(BorderFactory.createTitledBorder("Eventos Disponibles (Zona | Stock)"));
        add(scroll, BorderLayout.CENTER);

        // --- BOTONES ---
        JPanel panelBotones = new JPanel();
        JButton btnCrear = new JButton("Crear Evento");
        JButton btnReservar = new JButton("Reservar");
        JButton btnMisReservas = new JButton("Mis Reservas");

        btnReservar.setBackground(new Color(46, 204, 113));
        btnMisReservas.setBackground(new Color(52, 152, 219));

        panelBotones.add(btnCrear);
        panelBotones.add(btnReservar);
        panelBotones.add(btnMisReservas);
        add(panelBotones, BorderLayout.SOUTH);

        // --- LÓGICA DE HISTORIAS DE USUARIO ---

        btnCrear.addActionListener(e -> {
            try {
                String nombreEv = JOptionPane.showInputDialog(this, "Nombre del Evento:");
                if (nombreEv == null || nombreEv.isEmpty()) return;

                Evento nuevoEv = new Evento();
                nuevoEv.setNombre(nombreEv);
                List<ZonaEvento> zonas = new ArrayList<>();

                boolean masZonas = true;
                while (masZonas) {
                    String nZona = JOptionPane.showInputDialog(this, "Nombre de zona (VIP, General...):");
                    int cap = Integer.parseInt(JOptionPane.showInputDialog(this, "Capacidad total:"));

                    ZonaEvento ze = new ZonaEvento();
                    ze.setNombreZona(nZona);
                    ze.setCapacidadTotal(cap);
                    ze.setBoletasDisponibles(cap);
                    zonas.add(ze);

                    int r = JOptionPane.showConfirmDialog(this, "¿Añadir otra zona?", "Zonas", JOptionPane.YES_NO_OPTION);
                    if (r != JOptionPane.YES_OPTION) masZonas = false;
                }

                nuevoEv.setZonas(zonas);
                gestorEvento.registrarEvento(nuevoEv);
                DataStorage.EVENTOS.add(nuevoEv);

                actualizarLista();
                ArchivoUtil.guardarDatos();
                JOptionPane.showMessageDialog(this, "Evento creado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Datos numéricos inválidos.");
            }
        });

        btnReservar.addActionListener(e -> {
            int selIndex = listaEventos.getSelectedIndex();
            if (selIndex == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un evento de la lista.");
                return;
            }

            Evento ev = DataStorage.EVENTOS.get(selIndex);
            String[] opcionesZonas = ev.getZonas().stream().map(ZonaEvento::getNombreZona).toArray(String[]::new);
            String zonaElegida = (String) JOptionPane.showInputDialog(this, "Zona:", "Localidad",
                    JOptionPane.QUESTION_MESSAGE, null, opcionesZonas, opcionesZonas[0]);

            if (zonaElegida == null) return;

            try {
                String nom = JOptionPane.showInputDialog(this, "Nombre Cliente:");
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "ID / Cédula:"));
                int cant = Integer.parseInt(JOptionPane.showInputDialog(this, "Cantidad (Máx 10):"));

                Usuario u = new Usuario();
                u.setNombre(nom);
                u.setNumeroIdentificacion(id);

                String respuesta = gestorVenta.procesarReserva(u, ev, zonaElegida, cant);
                JOptionPane.showMessageDialog(this, respuesta);

                actualizarLista();
                ArchivoUtil.guardarDatos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos de reserva incorrectos.");
            }
        });

        btnMisReservas.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(this, "Ingrese ID para consultar:");
            if (idStr == null) return;

            try {
                int idBusca = Integer.parseInt(idStr);
                boolean encontro = false;

                for (Venta v : DataStorage.VENTAS) {
                    if (v.getCliente().getNumeroIdentificacion() == idBusca) {
                        encontro = true;
                        String detalle = "Reserva: " + v.getIdVenta().substring(0, 5) + "...\n" +
                                "Evento: " + v.getEvento().getNombre() + "\n" +
                                "Zona: " + v.getNombreZona() + "\n" +
                                "Cantidad: " + v.getCantidadReservada() + "\n" +
                                "Estado: " + v.getEstado();

                        if (v.getEstado() == EstadoVenta.RESERVADA) {
                            int pagar = JOptionPane.showConfirmDialog(this, detalle + "\n\n¿Desea PAGAR ahora?", "Pago", JOptionPane.YES_NO_OPTION);
                            if (pagar == JOptionPane.YES_OPTION) {
                                v.setEstado(EstadoVenta.PAGADA);
                                JOptionPane.showMessageDialog(this, "✅ Pago exitoso.");
                                ArchivoUtil.guardarDatos();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, detalle);
                        }
                    }
                }
                if (!encontro) JOptionPane.showMessageDialog(this, "No hay registros.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID no válido.");
            }
        });
    }

    private void actualizarLista() {
        modeloLista.clear();
        for (Evento e : DataStorage.EVENTOS) {
            StringBuilder sb = new StringBuilder(e.getNombre() + " | ");
            for (ZonaEvento z : e.getZonas()) {
                sb.append(z.getNombreZona()).append(": ").append(z.getBoletasDisponibles()).append("  ");
            }
            modeloLista.addElement(sb.toString());
        }
    }
}
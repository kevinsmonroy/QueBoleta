package com.queboleta.view;

import com.queboleta.entity.Event;
import com.queboleta.entity.EventZone;
import com.queboleta.entity.Sale;
import com.queboleta.entity.SaleStatus;
import com.queboleta.entity.User;
import org.springframework.stereotype.Component;
import com.queboleta.service.EventService;
import com.queboleta.service.ExpirationService;
import com.queboleta.service.SaleService;
import com.queboleta.util.Configuracion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class WindowView extends JFrame {

    // ── Servicios ────────────────────────────────────────────────────────────────
    private final EventService eventService;
    private final SaleService saleService;
    private final ExpirationService expirationService;
    private final Configuracion configuracion;

    // ── Zonas fijas RQ-01 ────────────────────────────────────────────────────────
    private static final String ZONA_A = "Zona A";
    private static final String ZONA_B = "Zona B";
    private static final String ZONA_C = "Zona C";
    private static final int CAP_A = 200;
    private static final int CAP_B = 100;
    private static final int CAP_C = 50;

    // ── Tabla cartelera ──────────────────────────────────────────────────────────
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"#", "Evento", "Fecha", "Hora", "Lugar",
                    "Zona A (disp/total — $)", "Zona B (disp/total — $)", "Zona C (disp/total — $)"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tablaEventos = new JTable(modeloTabla);

    // ── Constructor ──────────────────────────────────────────────────────────────
    public WindowView(EventService eventService, SaleService saleService,
                      ExpirationService expirationService) {
        this.eventService      = eventService;
        this.saleService       = saleService;
        this.expirationService = expirationService;
        this.configuracion     = new Configuracion();

        construirUI();
        actualizarTabla();
        setVisible(true);
    }

    // ── Construcción de UI ───────────────────────────────────────────────────────
    private void construirUI() {
        setTitle("QueBoleta! — Sistema de Boletas");
        setSize(1050, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("SISTEMA QUEBOLETA!", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(41, 128, 185));
        add(titulo, BorderLayout.NORTH);

        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.setRowHeight(26);
        tablaEventos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaEventos.setFont(new Font("Arial", Font.PLAIN, 12));
        add(new JScrollPane(tablaEventos), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));

        JButton btnCrear      = boton("Registrar Evento",  new Color(52, 152, 219));
        JButton btnReservar   = boton("Realizar Reserva",  new Color(39, 174, 96));
        JButton btnPagar      = boton("Reportar Pago",     new Color(230, 126, 34));
        JButton btnMisCompras = boton("Mis Compras",       new Color(142, 68, 173));
        JButton btnRefrescar  = boton("Actualizar",        new Color(127, 140, 141));

        panelBotones.add(btnCrear);
        panelBotones.add(btnReservar);
        panelBotones.add(btnPagar);
        panelBotones.add(btnMisCompras);
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCrear.addActionListener(e      -> dialogRegistrarEvento());
        btnReservar.addActionListener(e   -> dialogRealizarReserva());
        btnPagar.addActionListener(e      -> dialogReportarPago());
        btnMisCompras.addActionListener(e -> dialogVerMisCompras());
        btnRefrescar.addActionListener(e  -> { expirationService.processExpirations(); actualizarTabla(); });
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(170, 36));
        return b;
    }

    // ── RQ-05: Actualizar tabla ───────────────────────────────────────────────────
    private void actualizarTabla() {
        expirationService.processExpirations();
        modeloTabla.setRowCount(0);
        List<Event> eventos = eventService.getAllEvents();
        if (eventos == null) return;

        DateTimeFormatter fFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fHora  = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < eventos.size(); i++) {
            Event ev = eventos.get(i);
            String dA = "-", dB = "-", dC = "-";
            if (ev.getZones() != null) {
                for (EventZone z : ev.getZones()) {
                    String d = z.getAvailableTickets() + "/" + z.getTotalCapacity()
                            + " — $" + (int) z.getPrice();
                    if (z.getZoneName().equals(ZONA_A))      dA = d;
                    else if (z.getZoneName().equals(ZONA_B)) dB = d;
                    else if (z.getZoneName().equals(ZONA_C)) dC = d;
                }
            }
            modeloTabla.addRow(new Object[]{
                    i + 1,
                    ev.getName(),
                    ev.getDate() != null ? ev.getDate().format(fFecha) : "",
                    ev.getHour() != null ? ev.getHour().format(fHora)  : "",
                    ev.getVenue() != null ? ev.getVenue() : "",
                    dA, dB, dC
            });
        }
    }

    // ── RQ-01: Registrar Evento ───────────────────────────────────────────────────
    private void dialogRegistrarEvento() {
        JTextField txtNombre  = new JTextField();
        JTextField txtFecha   = new JTextField("dd/MM/yyyy");
        JTextField txtHora    = new JTextField("HH:mm");
        JTextField txtLugar   = new JTextField();
        JTextField txtPrecioA = new JTextField();
        JTextField txtPrecioB = new JTextField();
        JTextField txtPrecioC = new JTextField();

        Object[] campos = {
                "Nombre del evento:",        txtNombre,
                "Fecha (dd/MM/yyyy):",       txtFecha,
                "Hora (HH:mm):",             txtHora,
                "Lugar:",                    txtLugar,
                "Precio Zona A (200 bol.):", txtPrecioA,
                "Precio Zona B (100 bol.):", txtPrecioB,
                "Precio Zona C  (50 bol.):", txtPrecioC,
        };

        if (JOptionPane.showConfirmDialog(this, campos, "Registrar Evento",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) { error("El nombre no puede estar vacío."); return; }

            LocalDate fecha  = LocalDate.parse(txtFecha.getText().trim(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalTime hora   = LocalTime.parse(txtHora.getText().trim(),
                    DateTimeFormatter.ofPattern("HH:mm"));
            String lugar     = txtLugar.getText().trim();
            double precioA   = Double.parseDouble(txtPrecioA.getText().trim());
            double precioB   = Double.parseDouble(txtPrecioB.getText().trim());
            double precioC   = Double.parseDouble(txtPrecioC.getText().trim());

            Event evento = new Event();
            evento.setName(nombre);
            evento.setDate(fecha);
            evento.setHour(hora);
            evento.setVenue(lugar);

            List<EventZone> zonas = new ArrayList<>();
            zonas.add(crearZona(ZONA_A, precioA, CAP_A, evento));
            zonas.add(crearZona(ZONA_B, precioB, CAP_B, evento));
            zonas.add(crearZona(ZONA_C, precioC, CAP_C, evento));
            evento.setZones(zonas);

            info(eventService.registerEvent(evento));
            actualizarTabla();

        } catch (DateTimeParseException ex) {
            error("Formato incorrecto. Use dd/MM/yyyy para fecha y HH:mm para hora.");
        } catch (NumberFormatException ex) {
            error("Los precios deben ser valores numéricos.");
        }
    }

    private EventZone crearZona(String nombre, double precio, int cap, Event evento) {
        EventZone z = new EventZone();
        z.setZoneName(nombre);
        z.setPrice(precio);
        z.setTotalCapacity(cap);
        z.setAvailableTickets(cap);
        z.setEvent(evento);
        return z;
    }

    // ── RQ-02: Realizar Reserva ───────────────────────────────────────────────────
    private void dialogRealizarReserva() {
        List<Event> eventos = eventService.getAllEvents();
        if (eventos == null || eventos.isEmpty()) {
            info("No hay eventos registrados todavía.");
            return;
        }

        String[] nombresEventos = eventos.stream()
                .map(ev -> ev.getName() + " (" + (ev.getDate() != null ? ev.getDate() : "") + ")")
                .toArray(String[]::new);

        JComboBox<String> cbEvento  = new JComboBox<>(nombresEventos);
        JTextField txtNombre        = new JTextField();
        JTextField txtCedula        = new JTextField();
        JComboBox<String> cbZona    = new JComboBox<>(new String[]{ZONA_A, ZONA_B, ZONA_C});
        JSpinner spinCantidad       = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JComboBox<String> cbMetodo  = new JComboBox<>(new String[]{"CREDIT_CARD", "DEBIT", "PSE"});

        Object[] campos = {
                "Evento:",            cbEvento,
                "Nombre:",            txtNombre,
                "Cedula:",            txtCedula,
                "Zona:",              cbZona,
                "Cantidad (max 10):", spinCantidad,
                "Metodo de pago:",    cbMetodo,
        };

        if (JOptionPane.showConfirmDialog(this, campos, "Realizar Reserva",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            Event eventoSel = eventos.get(cbEvento.getSelectedIndex());
            String nombre   = txtNombre.getText().trim();
            long cedula     = Long.parseLong(txtCedula.getText().trim());
            String zona     = (String) cbZona.getSelectedItem();
            int cantidad    = (int) spinCantidad.getValue();

            if (nombre.isEmpty()) { error("El nombre no puede estar vacio."); return; }

            User cliente  = new User(cedula, nombre, "", "");
            String result = saleService.processReservation(cliente, eventoSel, zona, cantidad);

            if (result.startsWith("SUCCESS")) {
                info(result + "\n\nSu reserva expira en "
                        + configuracion.getTiempoReservaHoras() + " hora(s) si no completa el pago.");
                actualizarTabla();
            } else {
                error(result);
            }
        } catch (NumberFormatException ex) {
            error("La cedula debe ser un numero valido.");
        }
    }

    // ── RQ-03: Reportar Pago ─────────────────────────────────────────────────────
    private void dialogReportarPago() {
        String cedulaStr = JOptionPane.showInputDialog(this, "Ingrese su cedula:");
        if (cedulaStr == null || cedulaStr.trim().isEmpty()) return;

        long cedula;
        try { cedula = Long.parseLong(cedulaStr.trim()); }
        catch (NumberFormatException e) { error("Cedula invalida."); return; }

        List<Sale> reservadas = saleService.getSalesByUser(cedula).stream()
                .filter(s -> s.getStatus() == SaleStatus.RESERVED)
                .toList();

        if (reservadas.isEmpty()) {
            info("No tiene reservas pendientes de pago para esa cedula.");
            return;
        }

        String[] opciones = reservadas.stream()
                .map(s -> "ID: " + s.getId().substring(0, 8)
                        + "  |  Zona: " + s.getZoneName()
                        + "  |  Cant: " + s.getQuantity())
                .toArray(String[]::new);

        String sel = (String) JOptionPane.showInputDialog(this,
                "Seleccione la reserva:", "Reportar Pago",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sel == null) return;

        Sale venta = reservadas.get(Arrays.asList(opciones).indexOf(sel));

        JTextField txtComprobante = new JTextField();
        JTextField txtValor       = new JTextField();

        if (JOptionPane.showConfirmDialog(this,
                new Object[]{"N comprobante:", txtComprobante, "Valor pagado ($):", txtValor},
                "Confirmar Pago", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            double valor = Double.parseDouble(txtValor.getText().trim());
            info(saleService.reportPayment(venta, valor, txtComprobante.getText().trim()));
            actualizarTabla();
        } catch (NumberFormatException ex) {
            error("El valor pagado debe ser numerico.");
        }
    }

    // ── RQ-04: Ver Mis Compras ────────────────────────────────────────────────────
    private void dialogVerMisCompras() {
        String cedulaStr = JOptionPane.showInputDialog(this, "Ingrese su cedula:");
        if (cedulaStr == null || cedulaStr.trim().isEmpty()) return;

        long cedula;
        try { cedula = Long.parseLong(cedulaStr.trim()); }
        catch (NumberFormatException e) { error("Cedula invalida."); return; }

        List<Sale> ventas = saleService.getSalesByUser(cedula);
        if (ventas == null || ventas.isEmpty()) {
            info("No se encontraron compras para esa cedula.");
            return;
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID (parcial)", "Zona", "Boletas", "Estado", "Fecha Reserva"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Sale s : ventas) {
            modelo.addRow(new Object[]{
                    s.getId().substring(0, 8) + "...",
                    s.getZoneName(),
                    s.getQuantity(),
                    s.getStatus(),
                    s.getReservationDate() != null ? s.getReservationDate().format(fmt) : ""
            });
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(620, 220));

        JOptionPane.showMessageDialog(this, scroll,
                "Mis Compras — ordenadas por fecha", JOptionPane.PLAIN_MESSAGE);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────
    private void info(String msg)  { JOptionPane.showMessageDialog(this, msg, "Info",  JOptionPane.INFORMATION_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}
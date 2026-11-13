package vista;

import controlador.NotificacionesController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Notificacion;
import modelo.Usuario;

/** Bandeja personal. La vista delega las consultas y cambios al controlador. */
public class NotificacionesView extends JPanel {
    private final Usuario usuario;
    private final NotificacionesController controlador;
    private final Runnable alCambiar;
    private final JLabel resumen = new JLabel();
    private final JCheckBox soloPendientes = new JCheckBox("Solo pendientes");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Estado", "Fecha", "Tipo", "Título"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea mensaje = new JTextArea();
    private final JButton marcar = new JButton("Marcar seleccionada como leída");
    private ArrayList<Notificacion> visibles = new ArrayList<>();

    public NotificacionesView(Usuario usuario, NotificacionesController controlador,
            Runnable alCambiar, Runnable verInvitaciones) {
        this.usuario = usuario;
        this.controlador = controlador;
        this.alCambiar = alCambiar;
        setLayout(new BorderLayout(12, 12));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel cabecera = new JPanel(new BorderLayout(6, 6));
        cabecera.setOpaque(false);
        JLabel titulo = new JLabel("Mis notificaciones");
        titulo.setFont(Estilos.SUBTITULO);
        titulo.setForeground(Estilos.VERDE_OSCURO);
        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(resumen, BorderLayout.CENTER);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setOpaque(false);
        soloPendientes.setName("soloNotificacionesPendientes");
        soloPendientes.setOpaque(false);
        soloPendientes.addActionListener(e -> refrescar());
        filtros.add(soloPendientes);
        JButton actualizar = new JButton("Actualizar");
        Estilos.estilizarBoton(actualizar);
        actualizar.addActionListener(e -> refrescar());
        filtros.add(actualizar);
        cabecera.add(filtros, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        tabla.setName("tablaNotificaciones");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(300);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarSeleccion();
        });
        mensaje.setName("mensajeNotificacion");
        mensaje.setEditable(false);
        mensaje.setLineWrap(true);
        mensaje.setWrapStyleWord(true);
        mensaje.setMargin(new java.awt.Insets(10, 10, 10, 10));
        mensaje.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JScrollPane detalle = new JScrollPane(mensaje);
        detalle.setPreferredSize(new Dimension(400, 125));
        detalle.setBorder(BorderFactory.createTitledBorder("Detalle de la notificación"));
        panel.add(detalle, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        acciones.setOpaque(false);
        Estilos.estilizarBoton(marcar);
        marcar.setName("marcarNotificacionLeida");
        marcar.addActionListener(e -> ejecutar(() -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) controlador.marcarLeida(usuario, visibles.get(fila));
        }));
        acciones.add(marcar);
        JButton todas = new JButton("Marcar todas como leídas");
        todas.setName("marcarTodasLeidas");
        Estilos.estilizarBoton(todas);
        todas.addActionListener(e -> ejecutar(() -> controlador.marcarTodasLeidas(usuario)));
        acciones.add(todas);
        if (usuario.getRango().puedeUnirseAClubes()) {
            JButton invitaciones = new JButton("Ver invitaciones de liderazgo");
            Estilos.estilizarBoton(invitaciones);
            invitaciones.addActionListener(e -> verInvitaciones.run());
            acciones.add(invitaciones);
        }
        // Dos filas permiten usar los controles en el tamaño mínimo de la ventana.
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        JPanel lectura = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        lectura.setOpaque(false);
        lectura.add(marcar);
        lectura.add(todas);
        pie.add(lectura, BorderLayout.NORTH);
        pie.add(acciones, BorderLayout.SOUTH);
        add(pie, BorderLayout.SOUTH);
        refrescar();
    }

    private void ejecutar(Runnable accion) {
        try { accion.run(); refrescar(); }
        catch (RuntimeException ex) { VentanaPrincipal.mostrarError(this, ex); }
    }

    public void refrescar() {
        visibles = controlador.obtenerNotificaciones(usuario, soloPendientes.isSelected());
        modelo.setRowCount(0);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Notificacion aviso : visibles) {
            modelo.addRow(new Object[] {aviso.isLeida() ? "Leída" : "Pendiente",
                    aviso.getFecha().format(formato), aviso.getTipo(), aviso.getTitulo()});
        }
        int pendientes = controlador.contarPendientes(usuario);
        resumen.setText(visibles.isEmpty()
                ? (soloPendientes.isSelected() ? "No tienes notificaciones pendientes." : "Aún no tienes notificaciones.")
                : pendientes + " pendientes · " + visibles.size() + " en esta lista");
        mensaje.setText("Selecciona una notificación para leer su contenido.");
        marcar.setEnabled(false);
        alCambiar.run();
    }

    private void mostrarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= visibles.size()) { marcar.setEnabled(false); return; }
        Notificacion aviso = visibles.get(fila);
        mensaje.setText(aviso.getTitulo() + "\n\n" + aviso.getMensaje());
        mensaje.setCaretPosition(0);
        marcar.setEnabled(!aviso.isLeida());
    }
}

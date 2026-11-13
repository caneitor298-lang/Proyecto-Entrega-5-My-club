package vista;

import modelo.Club;
import modelo.SolicitudLider;
import controlador.SolicitudesController;
import modelo.Usuario;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Pantalla para que un estudiante vea las invitaciones de liderazgo
 * enviadas por el administrador y las acepte o rechace.
 */
public class InvitacionesView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final Usuario estudiante;
    private final SolicitudesController controlador;
    private final Runnable alAceptarYCerrarSesion; // tras aceptar, hay que reabrir sesión como líder
    private final Runnable volver;
    private final DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DefaultListModel<SolicitudLider> modelo = new DefaultListModel<>();
    private final JList<SolicitudLider> lista = new JList<>(modelo);
    private final JTextArea detalle = new JTextArea();
    private final JButton aceptar = new JButton("Aceptar cargo de líder");
    private final JButton rechazar = new JButton("Rechazar invitación");
    private final JLabel resumen = new JLabel();

    public InvitacionesView(Usuario estudiante, SolicitudesController controlador,
                            Runnable alAceptarYCerrarSesion, Runnable volver) {
        this.estudiante = estudiante;
        this.controlador = controlador;
        this.alAceptarYCerrarSesion = alAceptarYCerrarSesion;
        this.volver = volver;

        setLayout(new BorderLayout(14, 14));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel cabecera = new JPanel(new BorderLayout(8, 8));
        cabecera.setOpaque(false);
        JLabel titulo = new JLabel("Invitaciones de liderazgo");
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.VERDE_OSCURO);
        cabecera.add(titulo, BorderLayout.NORTH);
        JLabel sub = new JLabel("El administrador te ha propuesto como nuevo líder de un club. Puedes aceptar o rechazar.");
        sub.setFont(Estilos.TEXTO);
        cabecera.add(sub, BorderLayout.CENTER);
        resumen.setFont(Estilos.TEXTO);
        resumen.setForeground(Estilos.AZUL_OSCURO);
        cabecera.add(resumen, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(Estilos.TEXTO);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SolicitudLider) {
                    SolicitudLider s = (SolicitudLider) value;
                    setText(s.getNombreClub() + "  ·  " + fecha.format(s.getFechaSolicitud()));
                }
                return this;
            }
        });
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalle();
        });
        JScrollPane scrollLista = new JScrollPane(lista);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Invitaciones pendientes"));
        scrollLista.setPreferredSize(new Dimension(280, 180));

        detalle.setEditable(false);
        detalle.setFont(Estilos.TEXTO);
        detalle.setLineWrap(true);
        detalle.setWrapStyleWord(true);
        detalle.setMargin(new Insets(12, 14, 12, 14));
        JScrollPane scrollDetalle = new JScrollPane(detalle);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Detalle de la invitación"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, scrollDetalle);
        split.setResizeWeight(0.35);
        split.setDividerLocation(280);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        JButton actualizar = new JButton("Actualizar");
        Estilos.estilizarBoton(actualizar);
        actualizar.addActionListener(e -> refrescar());
        JButton btnVolver = new JButton("Volver");
        Estilos.estilizarBotonSecundario(btnVolver);
        btnVolver.addActionListener(e -> volver.run());
        Estilos.estilizarBoton(aceptar);
        aceptar.setBackground(new Color(30, 125, 85));
        Estilos.estilizarBoton(rechazar);
        rechazar.setBackground(new Color(175, 55, 65));
        aceptar.setEnabled(false);
        rechazar.setEnabled(false);
        aceptar.addActionListener(e -> aceptarSeleccionada());
        rechazar.addActionListener(e -> rechazarSeleccionada());
        acciones.add(actualizar);
        acciones.add(btnVolver);
        acciones.add(rechazar);
        acciones.add(aceptar);
        add(acciones, BorderLayout.SOUTH);

        refrescar();
    }

    public void refrescar() {
        modelo.clear();
        ArrayList<SolicitudLider> invitaciones = controlador.obtenerInvitacionesPendientes(estudiante);
        for (SolicitudLider s : invitaciones) {
            modelo.addElement(s);
        }
        resumen.setText("Invitaciones pendientes: " + invitaciones.size());
        if (!invitaciones.isEmpty()) {
            lista.setSelectedIndex(0);
        } else {
            detalle.setText("No tienes invitaciones de liderazgo pendientes.");
            aceptar.setEnabled(false);
            rechazar.setEnabled(false);
        }
    }

    private void mostrarDetalle() {
        SolicitudLider s = lista.getSelectedValue();
        boolean hay = s != null;
        aceptar.setEnabled(hay);
        rechazar.setEnabled(hay);
        if (!hay) {
            detalle.setText("Selecciona una invitación de la lista.");
            return;
        }
        String texto = "INVITACIÓN PARA SER LÍDER\n\n"
                + "Club: " + s.getNombreClub()
                + "\nCategoría: " + s.getCategoria()
                + "\nUbicación: " + s.getUbicacion()
                + "\nHorario: " + s.getHorario()
                + "\n\nDescripción:\n" + s.getDescripcion()
                + "\n\nFecha de la invitación: " + fecha.format(s.getFechaSolicitud())
                + "\n\nSi aceptas, tu cuenta pasará a rango Líder y podrás administrar este club "
                + "(anuncios, horario, información). Dejarás de figurar como miembro.";
        detalle.setText(texto);
        detalle.setCaretPosition(0);
    }

    private void aceptarSeleccionada() {
        SolicitudLider s = lista.getSelectedValue();
        if (s == null) return;
        int r = JOptionPane.showConfirmDialog(this,
                "¿Aceptar el cargo de líder del club \"" + s.getNombreClub() + "\"?\n\n"
                + "Tu cuenta pasará a rango Líder y podrás administrarlo.\n"
                + "Tendrás que volver a iniciar sesión para ver los cambios.",
                "Aceptar cargo de líder", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            controlador.aceptarInvitacion(estudiante, s);
            JOptionPane.showMessageDialog(this,
                    "¡Felicidades! Ahora eres el líder de \"" + s.getNombreClub() + "\".\n"
                    + "Cierra sesión e inicia de nuevo para ver el menú de líder.",
                    "Cargo aceptado", JOptionPane.INFORMATION_MESSAGE);
            alAceptarYCerrarSesion.run();
        } catch (RuntimeException ex) {
            VentanaPrincipal.mostrarError(this, ex);
            refrescar();
        }
    }

    private void rechazarSeleccionada() {
        SolicitudLider s = lista.getSelectedValue();
        if (s == null) return;
        JTextArea motivo = new JTextArea(4, 28);
        motivo.setLineWrap(true);
        motivo.setWrapStyleWord(true);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("Motivo del rechazo (opcional):"), BorderLayout.NORTH);
        panel.add(new JScrollPane(motivo), BorderLayout.CENTER);
        int r = JOptionPane.showConfirmDialog(this, panel, "Rechazar invitación",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        try {
            controlador.rechazarInvitacion(estudiante, s, motivo.getText());
            JOptionPane.showMessageDialog(this, "Has rechazado la invitación.",
                    "Invitación rechazada", JOptionPane.INFORMATION_MESSAGE);
            refrescar();
        } catch (RuntimeException ex) {
            VentanaPrincipal.mostrarError(this, ex);
        }
    }
}

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AdministradorView extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Usuario administrador;
    private final SolicitudesController controlador;
    private final DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JComboBox<String> filtro = new JComboBox<>(new String[] {
            "Pendientes", "Aceptadas", "Denegadas", "Todas" });
    private final DefaultTableModel modelo = new DefaultTableModel(new String[] {
            "Solicitante", "Carné", "Tipo / Club", "Fecha", "Estado" }, 0) {
        private static final long serialVersionUID = 1L;
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea detalle = new JTextArea();
    private final JButton aceptar = new JButton("Aceptar solicitud");
    private final JButton denegar = new JButton("Denegar solicitud");
    private final JLabel resumen = new JLabel();
    private ArrayList<SolicitudLider> solicitudes = new ArrayList<>();

    private final DefaultListModel<Club> modeloClubes = new DefaultListModel<>();
    private final JList<Club> listaClubes = new JList<>(modeloClubes);
    private final DefaultListModel<Estudiante> modeloMiembros = new DefaultListModel<>();
    private final JList<Estudiante> listaMiembros = new JList<>(modeloMiembros);
    private final JLabel infoClub = new JLabel(" ");
    private final JButton btnInvitar = new JButton("Enviar invitación de liderazgo");
    private final JLabel resumenClubes = new JLabel();

    public AdministradorView(Usuario administrador, SolicitudesController controlador) {
        this.administrador = administrador;
        this.controlador = controlador;
        setLayout(new BorderLayout(14, 14));
        setBackground(Estilos.FONDO_VERDE);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(Estilos.TEXTO);
        pestanas.addTab("Solicitudes", crearPanelSolicitudes());
        pestanas.addTab("Clubes sin líder", crearPanelClubesSinLider());
        add(pestanas, BorderLayout.CENTER);

        pestanas.addChangeListener(e -> {
            if (pestanas.getSelectedIndex() == 1) {
                refrescarClubesSinLider();
            }
        });

        refrescar();
        refrescarClubesSinLider();
    }

    private JPanel crearPanelSolicitudes() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JPanel cabecera = new JPanel(new BorderLayout(8, 12));
        cabecera.setOpaque(false);
        JPanel titulo = new JPanel(new GridLayout(0, 1, 0, 6));
        titulo.setOpaque(false);
        JLabel nombre = new JLabel("Solicitudes de líderes");
        nombre.setFont(Estilos.TITULO);
        nombre.setForeground(Estilos.VERDE);
        titulo.add(nombre);
        titulo.add(new JLabel("Acepta o deniega solicitudes de liderazgo y de renuncia."));
        cabecera.add(titulo, BorderLayout.NORTH);

        JPanel herramientas = new JPanel(new BorderLayout(12, 8));
        herramientas.setOpaque(false);
        resumen.setFont(Estilos.TEXTO);
        resumen.setForeground(Estilos.AZUL_OSCURO);
        herramientas.add(resumen, BorderLayout.NORTH);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Mostrar:"));
        filtro.setName("filtroSolicitudes");
        filtros.add(filtro);
        JButton actualizar = new JButton("Actualizar lista");
        actualizar.addActionListener(e -> refrescar());
        Estilos.estilizarBoton(actualizar);
        filtros.add(actualizar);
        herramientas.add(filtros, BorderLayout.CENTER);
        cabecera.add(herramientas, BorderLayout.CENTER);
        panel.add(cabecera, BorderLayout.NORTH);

        tabla.setName("tablaSolicitudes");
        tabla.setRowHeight(34);
        tabla.setFont(Estilos.TEXTO);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        Estilos.estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(65);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(135);
        JScrollPane lista = new JScrollPane(tabla);
        lista.setColumnHeaderView(tabla.getTableHeader());
        lista.setMinimumSize(new Dimension(300, 130));

        detalle.setName("detalleSolicitud");
        detalle.setEditable(false);
        detalle.setFont(Estilos.TEXTO);
        detalle.setLineWrap(true);
        detalle.setWrapStyleWord(true);
        detalle.setMargin(new Insets(14, 16, 14, 16));
        JScrollPane datosSolicitud = new JScrollPane(detalle);
        datosSolicitud.setMinimumSize(new Dimension(300, 130));
        datosSolicitud.setBorder(BorderFactory.createTitledBorder("Datos de la solicitud"));
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lista, datosSolicitud);
        division.setResizeWeight(0.45);
        division.setDividerLocation(180);
        division.setBorder(null);
        panel.add(division, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        Estilos.estilizarBoton(aceptar);
        aceptar.setBackground(new Color(30, 125, 85));
        Estilos.estilizarBoton(denegar);
        denegar.setBackground(new Color(175, 55, 65));
        aceptar.setName("aceptarSolicitud");
        denegar.setName("denegarSolicitud");
        acciones.add(denegar);
        acciones.add(aceptar);
        panel.add(acciones, BorderLayout.SOUTH);

        filtro.addActionListener(e -> refrescar());
        tabla.getSelectionModel().addListSelectionListener(e -> mostrarSeleccionada());
        aceptar.addActionListener(e -> aceptarSeleccionada());
        denegar.addActionListener(e -> denegarSeleccionada());

        return panel;
    }
    private JPanel crearPanelClubesSinLider() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JPanel cabecera = new JPanel(new BorderLayout(8, 8));
        cabecera.setOpaque(false);
        JLabel titulo = new JLabel("Clubes sin líder");
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.VERDE);
        cabecera.add(titulo, BorderLayout.NORTH);
        JLabel sub = new JLabel("Tras una renuncia, el club queda abandonado. Aquí puedes designar a un miembro como nuevo líder.");
        sub.setFont(Estilos.TEXTO);
        cabecera.add(sub, BorderLayout.CENTER);
        resumenClubes.setFont(Estilos.TEXTO);
        resumenClubes.setForeground(Estilos.AZUL_OSCURO);
        cabecera.add(resumenClubes, BorderLayout.SOUTH);
        panel.add(cabecera, BorderLayout.NORTH);

        listaClubes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaClubes.setFont(Estilos.TEXTO);
        listaClubes.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Club) {
                    Club c = (Club) value;
                    setText(c.getNombre() + "  ·  " + c.getCategoria());
                }
                return this;
            }
        });
        listaClubes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarMiembrosDelClub();
        });
        JScrollPane scrollClubes = new JScrollPane(listaClubes);
        scrollClubes.setBorder(BorderFactory.createTitledBorder("Clubes abandonados"));
        scrollClubes.setPreferredSize(new Dimension(280, 200));

        JPanel derecha = new JPanel(new BorderLayout(8, 8));
        derecha.setOpaque(false);

        infoClub.setFont(Estilos.TEXTO);
        infoClub.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        derecha.add(infoClub, BorderLayout.NORTH);

        listaMiembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaMiembros.setFont(Estilos.TEXTO);
        listaMiembros.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Estudiante) {
                    Estudiante e = (Estudiante) value;
                    setText(e.getNombre() + "  <" + e.getCorreo() + ">");
                }
                return this;
            }
        });
        listaMiembros.addListSelectionListener(e -> actualizarBotonAsignar());
        JScrollPane scrollMiembros = new JScrollPane(listaMiembros);
        scrollMiembros.setBorder(BorderFactory.createTitledBorder("Miembros del club (elige uno)"));
        derecha.add(scrollMiembros, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        JButton actualizarClubes = new JButton("Actualizar");
        Estilos.estilizarBoton(actualizarClubes);
        actualizarClubes.addActionListener(e -> refrescarClubesSinLider());
        Estilos.estilizarBoton(btnInvitar);
        btnInvitar.setBackground(new Color(30, 125, 85));
        btnInvitar.setEnabled(false);
        btnInvitar.addActionListener(e -> invitarLiderSeleccionado());
        acciones.add(actualizarClubes);
        acciones.add(btnInvitar);
        derecha.add(acciones, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollClubes, derecha);
        split.setResizeWeight(0.35);
        split.setDividerLocation(280);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    private void refrescarClubesSinLider() {
        modeloClubes.clear();
        modeloMiembros.clear();
        infoClub.setText(" ");
        btnInvitar.setEnabled(false);
        try {
            ArrayList<Club> sinLider = controlador.obtenerClubesSinLider(administrador);
            for (Club c : sinLider) {
                modeloClubes.addElement(c);
            }
            resumenClubes.setText("Clubes sin líder: " + sinLider.size());
            if (!sinLider.isEmpty()) {
                listaClubes.setSelectedIndex(0);
            }
        } catch (RuntimeException ex) {
            resumenClubes.setText("Error al cargar: " + ex.getMessage());
        }
    }

    private void mostrarMiembrosDelClub() {
        modeloMiembros.clear();
        btnInvitar.setEnabled(false);
        Club club = listaClubes.getSelectedValue();
        if (club == null) {
            infoClub.setText("Selecciona un club de la lista.");
            return;
        }
        infoClub.setText("<html><b>" + club.getNombre() + "</b> · " + club.getCategoria()
                + "<br>" + club.getUbicacion() + " · " + club.getHorario() + "</html>");
        try {
            ArrayList<Estudiante> miembros = controlador.obtenerMiembrosClub(administrador, club);
            for (Estudiante e : miembros) {
                modeloMiembros.addElement(e);
            }
            if (miembros.isEmpty()) {
                infoClub.setText(infoClub.getText() + "<br><i>No hay miembros inscritos. "
                        + "Un estudiante debe unirse primero al club.</i>");
            }
        } catch (RuntimeException ex) {
            infoClub.setText("Error: " + ex.getMessage());
        }
    }

    private void actualizarBotonAsignar() {
        btnInvitar.setEnabled(listaClubes.getSelectedValue() != null
                && listaMiembros.getSelectedValue() != null);
    }

    private void invitarLiderSeleccionado() {
        Club club = listaClubes.getSelectedValue();
        Estudiante miembro = listaMiembros.getSelectedValue();
        if (club == null || miembro == null) return;

        String mensaje = "¿Enviar invitación a " + miembro.getNombre() + " para ser el nuevo líder del club \""
                + club.getNombre() + "\"?\n\n"
                + "• El estudiante recibirá la invitación y podrá aceptarla o rechazarla.\n"
                + "• Solo si la acepta pasará a ser líder del club.\n"
                + "• Mientras tanto el club seguirá sin líder.";
        int r = JOptionPane.showConfirmDialog(this, mensaje, "Enviar invitación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        try {
            controlador.enviarInvitacionLiderazgo(administrador, club, miembro);
            JOptionPane.showMessageDialog(this,
                    "Invitación enviada a " + miembro.getNombre() + ".\n"
                    + "Cuando inicie sesión podrá aceptarla o rechazarla.",
                    "Invitación enviada", JOptionPane.INFORMATION_MESSAGE);
            refrescarClubesSinLider();
            refrescar();
        } catch (RuntimeException ex) {
            VentanaPrincipal.mostrarError(this, ex);
        }
    }

    
    public void refrescar() {
        SolicitudLider anterior = seleccionada();
        ArrayList<SolicitudLider> todas = controlador.obtenerSolicitudes(administrador, null);
        int pendientes = 0, aceptadas = 0, denegadas = 0;
        for (SolicitudLider solicitud : todas) {
            switch (solicitud.getEstado()) {
                case PENDIENTE: pendientes++; break;
                case ACEPTADA: aceptadas++; break;
                case DENEGADA: denegadas++; break;
            }
        }
        resumen.setText("Pendientes: " + pendientes + "     Aceptadas: " + aceptadas + "     Denegadas: " + denegadas);
        EstadoSolicitud estado = filtro.getSelectedIndex() == 3 ? null
                : EstadoSolicitud.values()[filtro.getSelectedIndex()];
        java.util.List<? extends RowSorter.SortKey> orden = tabla.getRowSorter() != null
                ? tabla.getRowSorter().getSortKeys() : null;
        tabla.clearSelection();
        tabla.setRowSorter(null);
        modelo.setRowCount(0);
        solicitudes = controlador.obtenerSolicitudes(administrador, estado);
        for (SolicitudLider solicitud : solicitudes) {
            String tipoClub;
            if (solicitud.isEsRenuncia()) {
                tipoClub = "RENUNCIA · " + solicitud.getNombreClub();
            } else if (solicitud.isEsInvitacion()) {
                tipoClub = "INVITACIÓN · " + solicitud.getNombreClub();
            } else {
                tipoClub = solicitud.getNombreClub();
            }
            modelo.addRow(new Object[] { solicitud.getNombre(), solicitud.getCarnet(),
                    tipoClub, fecha.format(solicitud.getFechaSolicitud()), solicitud.getEstado() });
        }
        tabla.setRowSorter(new TableRowSorter<>(modelo));
        if (orden != null) tabla.getRowSorter().setSortKeys(orden);
        if (!solicitudes.isEmpty()) {
            int indice = anterior == null ? 0 : solicitudes.indexOf(anterior);
            if (indice < 0) indice = 0;
            int fila = tabla.convertRowIndexToView(indice);
            if (fila >= 0) tabla.setRowSelectionInterval(fila, fila);
        }
        mostrarSeleccionada();
    }

    private SolicitudLider seleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= tabla.getRowCount()) return null;
        int indice = tabla.convertRowIndexToModel(fila);
        return indice >= 0 && indice < solicitudes.size() ? solicitudes.get(indice) : null;
    }

    private void mostrarSeleccionada() {
        SolicitudLider solicitud = seleccionada();
        boolean pendiente = solicitud != null && solicitud.getEstado() == EstadoSolicitud.PENDIENTE;
        boolean esInvitacion = solicitud != null && solicitud.isEsInvitacion();
        aceptar.setEnabled(pendiente && !esInvitacion);
        denegar.setEnabled(pendiente);
        if (solicitud == null) {
            detalle.setText("No hay solicitudes para mostrar con este filtro.");
            return;
        }
        String tipo;
        if (solicitud.isEsRenuncia()) {
            tipo = "RENUNCIA AL LIDERAZGO (volver a Estudiante)";
        } else if (solicitud.isEsInvitacion()) {
            tipo = "INVITACIÓN DE LIDERAZGO (admin → estudiante)\n"
                    + "Solo el estudiante invitado puede aceptarla o rechazarla.";
        } else {
            tipo = "SOLICITUD DE LIDERAZGO (ascender a Líder)";
        }
        String texto = "TIPO: " + tipo
                + "\n\nSOLICITANTE / INVITADO\nNombre: " + solicitud.getNombre()
                + "\nCarné: " + solicitud.getCarnet() + "\nCorreo: " + solicitud.getCorreo()
                + "\nEdad: " + (solicitud.getEdad() > 0 ? solicitud.getEdad() : "No indicada")
                + "\nCarrera: " + solicitud.getCarrera()
                + "\n\nCLUB\nNombre: " + solicitud.getNombreClub()
                + "\nCategoría: " + solicitud.getCategoria() + "\nUbicación: " + solicitud.getUbicacion()
                + "\nHorario: " + solicitud.getHorario() + "\nDescripción: " + solicitud.getDescripcion()
                + "\n\nSEGUIMIENTO\nEstado: " + solicitud.getEstado()
                + "\nEnviada: " + fecha.format(solicitud.getFechaSolicitud());
        if (solicitud.getFechaRevision() != null) {
            texto += "\nRevisada: " + fecha.format(solicitud.getFechaRevision())
                    + "\nRespondido por: " + solicitud.getRevisadoPor();
        }
        if (solicitud.getEstado() == EstadoSolicitud.DENEGADA) {
            texto += "\nMotivo: " + solicitud.getMotivoDenegacion();
        }
        detalle.setText(texto);
        detalle.setCaretPosition(0);
    }

    private void aceptarSeleccionada() {
        SolicitudLider solicitud = seleccionada();
        if (solicitud == null) return;
        String mensaje;
        String mensajeExito;
        if (solicitud.isEsRenuncia()) {
            mensaje = "¿Degradar a " + solicitud.getNombre() + " de Líder a Estudiante?\n"
                    + "El club \"" + solicitud.getNombreClub() + "\" se mantendrá en el sistema "
                    + "(quedará sin líder; puedes asignar uno en la pestaña \"Clubes sin líder\").";
            mensajeExito = "La cuenta pasó a rango Estudiante. El club quedó abandonado.";
        } else {
            mensaje = "¿Crear el club " + solicitud.getNombreClub() + " y ascender a "
                    + solicitud.getNombre() + " de Estudiante a Líder?";
            mensajeExito = "La cuenta pasó a rango Líder y su club fue creado.";
        }
        int respuesta = JOptionPane.showConfirmDialog(this, mensaje, "Aceptar solicitud", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;
        try {
            controlador.aceptarSolicitud(administrador, solicitud);
            refrescar();
            if (solicitud.isEsRenuncia()) {
                refrescarClubesSinLider();
            }
            JOptionPane.showMessageDialog(this, mensajeExito);
        } catch (RuntimeException ex) {
            VentanaPrincipal.mostrarError(this, ex);
        }
    }

    private void denegarSeleccionada() {
        SolicitudLider solicitud = seleccionada();
        if (solicitud == null) return;
        JTextArea motivo = new JTextArea(5, 30);
        motivo.setLineWrap(true);
        motivo.setWrapStyleWord(true);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("Motivo de denegación (máximo 500 caracteres):"), BorderLayout.NORTH);
        panel.add(new JScrollPane(motivo), BorderLayout.CENTER);
        while (JOptionPane.showConfirmDialog(this, panel, "Denegar solicitud",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                controlador.denegarSolicitud(administrador, solicitud, motivo.getText());
                refrescar();
                JOptionPane.showMessageDialog(this, "La solicitud fue denegada.");
                return;
            } catch (RuntimeException ex) {
                VentanaPrincipal.mostrarError(this, ex);
            }
        }
    }
}

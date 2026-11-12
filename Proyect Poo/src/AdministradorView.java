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
            "Solicitante", "Carné", "Club propuesto", "Fecha", "Estado" }, 0) {
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

    public AdministradorView(Usuario administrador, SolicitudesController controlador) {
        this.administrador = administrador;
        this.controlador = controlador;
        setLayout(new BorderLayout(14, 14));
        setBackground(Estilos.FONDO_VERDE);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel cabecera = new JPanel(new BorderLayout(8, 12));
        cabecera.setOpaque(false);
        JPanel titulo = new JPanel(new GridLayout(0, 1, 0, 6));
        titulo.setOpaque(false);
        JLabel nombre = new JLabel("Solicitudes de líderes");
        nombre.setFont(Estilos.TITULO);
        nombre.setForeground(Estilos.VERDE);
        titulo.add(nombre);
        titulo.add(new JLabel("Revisa los datos antes de crear un nuevo club."));
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
        add(cabecera, BorderLayout.NORTH);

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
        add(division, BorderLayout.CENTER);

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
        add(acciones, BorderLayout.SOUTH);

        filtro.addActionListener(e -> refrescar());
        tabla.getSelectionModel().addListSelectionListener(e -> mostrarSeleccionada());
        aceptar.addActionListener(e -> aceptarSeleccionada());
        denegar.addActionListener(e -> denegarSeleccionada());
        refrescar();
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
        java.util.List<? extends RowSorter.SortKey> orden = tabla.getRowSorter().getSortKeys();
        tabla.clearSelection();
        tabla.setRowSorter(null);
        modelo.setRowCount(0);
        solicitudes = controlador.obtenerSolicitudes(administrador, estado);
        for (SolicitudLider solicitud : solicitudes) {
            modelo.addRow(new Object[] { solicitud.getNombre(), solicitud.getCarnet(),
                    solicitud.getNombreClub(), fecha.format(solicitud.getFechaSolicitud()), solicitud.getEstado() });
        }
        tabla.setRowSorter(new TableRowSorter<DefaultTableModel>(modelo));
        tabla.getRowSorter().setSortKeys(orden);
        if (!solicitudes.isEmpty()) {
            int indice = anterior == null ? 0 : solicitudes.indexOf(anterior);
            if (indice < 0) indice = 0;
            int fila = tabla.convertRowIndexToView(indice);
            tabla.setRowSelectionInterval(fila, fila);
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
        aceptar.setEnabled(pendiente);
        denegar.setEnabled(pendiente);
        if (solicitud == null) {
            detalle.setText("No hay solicitudes para mostrar con este filtro.");
            return;
        }
        String texto = "SOLICITANTE\nNombre: " + solicitud.getNombre()
                + "\nCarné: " + solicitud.getCarnet() + "\nCorreo: " + solicitud.getCorreo()
                + "\nEdad: " + (solicitud.getEdad() > 0 ? solicitud.getEdad() : "No indicada")
                + "\nCarrera: " + solicitud.getCarrera()
                + "\n\nCLUB PROPUESTO\nNombre: " + solicitud.getNombreClub()
                + "\nCategoría: " + solicitud.getCategoria() + "\nUbicación: " + solicitud.getUbicacion()
                + "\nHorario: " + solicitud.getHorario() + "\nDescripción: " + solicitud.getDescripcion()
                + "\n\nSEGUIMIENTO\nEstado: " + solicitud.getEstado()
                + "\nEnviada: " + fecha.format(solicitud.getFechaSolicitud());
        if (solicitud.getFechaRevision() != null) {
            texto += "\nRevisada: " + fecha.format(solicitud.getFechaRevision())
                    + "\nAdministrador: " + solicitud.getRevisadoPor();
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
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Crear el club " + solicitud.getNombreClub() + " y ascender a "
                + solicitud.getNombre() + " de Estudiante a Líder?", "Aceptar solicitud", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;
        try {
            controlador.aceptarSolicitud(administrador, solicitud);
            refrescar();
            JOptionPane.showMessageDialog(this, "La cuenta pasó a rango Líder y su club fue creado.");
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

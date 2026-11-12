import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class InicioViewExtra extends JPanel {
    private final ClubController controlador;
    private final JTextField buscar = new JTextField(20);
    private final JComboBox<String> categoria = new JComboBox<>(new String[] {
            "Todas", "Deportes", "Tecnología", "Arte", "Académicos", "Entretenimiento" });
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Club", "Categoría", "Horario", "Ubicación"}, 0) {
        public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel estado = new JLabel();
    private final JButton ver = new JButton("Ver club seleccionado");
    private final RenderizadorClubExtra renderizador = new RenderizadorClubExtra();
    private ArrayList<Club> resultados = new ArrayList<>();

    public InicioViewExtra(ClubController controlador, Consumer<Club> abrirClub) {
        this.controlador = controlador;
        setLayout(new BorderLayout(12, 12));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JLabel titulo = new JLabel("Explorar clubes");
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.AZUL_OSCURO);
        JPanel cabecera = new JPanel(new BorderLayout(10, 14));
        cabecera.add(titulo, BorderLayout.NORTH);
        JPanel filtros = new JPanel(new BorderLayout(10, 5));
        JPanel busqueda = new JPanel(new BorderLayout(4, 4));
        busqueda.add(new JLabel("Buscar por nombre"), BorderLayout.NORTH);
        busqueda.add(buscar, BorderLayout.CENTER);
        filtros.add(busqueda, BorderLayout.CENTER);
        JPanel categorias = new JPanel(new BorderLayout(4, 4));
        categorias.add(new JLabel("Categoría"), BorderLayout.NORTH);
        categorias.add(categoria, BorderLayout.CENTER);
        filtros.add(categorias, BorderLayout.EAST);
        cabecera.add(filtros, BorderLayout.CENTER);
        add(cabecera, BorderLayout.NORTH);
        tabla.setRowHeight(40);
        tabla.setFont(Estilos.TEXTO);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(Estilos.AZUL_CLARO);
        tabla.setSelectionForeground(Estilos.AZUL_OSCURO);
        tabla.getTableHeader().setFont(Estilos.BOTON);
        tabla.getTableHeader().setBackground(Estilos.AZUL_OSCURO);
        tabla.getTableHeader().setForeground(Estilos.BLANCO);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setCellRenderer(renderizador);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(250);
        tabla.setFillsViewportHeight(true);
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(Estilos.BORDE, 1));
        desplazamiento.getViewport().setBackground(Estilos.BLANCO);
        add(desplazamiento, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout());
        pie.add(estado, BorderLayout.CENTER);
        Estilos.estilizarBoton(ver);
        ver.setPreferredSize(new Dimension(190, 38));
        pie.add(ver, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
        buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refrescar(); }
            public void removeUpdate(DocumentEvent e) { refrescar(); }
            public void changedUpdate(DocumentEvent e) { refrescar(); }
        });
        categoria.addActionListener(e -> refrescar());
        tabla.getSelectionModel().addListSelectionListener(e -> ver.setEnabled(tabla.getSelectedRow() >= 0));
        ver.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) abrirClub.accept(resultados.get(fila));
        });
        refrescar();
    }

    public void refrescar() {
        resultados = controlador.buscarClubes(buscar.getText(), (String) categoria.getSelectedItem());
        renderizador.limpiar();
        modelo.setRowCount(0);
        for (Club club : resultados)
            modelo.addRow(new Object[] {club, club.getCategoria(), club.getHorario(), club.getUbicacion()});
        estado.setText(resultados.isEmpty() ? "No se encontraron clubes con esos filtros." : resultados.size() + " clubes encontrados.");
        ver.setEnabled(false);
        if (!resultados.isEmpty()) tabla.setRowSelectionInterval(0, 0);
    }
}
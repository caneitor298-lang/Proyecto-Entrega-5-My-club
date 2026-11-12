import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class EditarAnunciosViewExtra extends JPanel {
    private Lider lider;
    private Club club;
    private AnunciosController controlador;
    private ExtrasClub extras = ExtrasClub.obtener();
    private DefaultListModel<String> modelo = new DefaultListModel<>();
    private JList<String> lista = new JList<>(modelo);
    private JTextField campoTitulo = new JTextField();
    private JTextArea campoMensaje = new JTextArea(8, 30);
    private SelectorImagenExtra selectorImagen = new SelectorImagenExtra(120, 90, "Adjuntar imagen");

    public EditarAnunciosViewExtra(Lider lider, DatosAplicacion datos, Runnable volver) {
        this.lider = lider;
        this.club = lider.getClubAdministrado();
        this.controlador = new AnunciosController(datos);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        JPanel cabecera = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Anuncios de " + club.getNombre());
        titulo.setFont(Estilos.SUBTITULO);
        cabecera.add(titulo, BorderLayout.CENTER);
        JButton regresar = new JButton("Volver al club");
        regresar.addActionListener(e -> volver.run());
        cabecera.add(regresar, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(lista);
        scrollLista.setPreferredSize(new Dimension(280, 0));
        add(scrollLista, BorderLayout.WEST);

        campoMensaje.setLineWrap(true);
        campoMensaje.setWrapStyleWord(true);
        JPanel arriba = new JPanel(new GridLayout(0, 1));
        arriba.add(new JLabel("Título:"));
        arriba.add(campoTitulo);
        arriba.add(new JLabel("Mensaje:"));
        JPanel centro = new JPanel(new BorderLayout(5, 5));
        centro.add(arriba, BorderLayout.NORTH);
        centro.add(new JScrollPane(campoMensaje), BorderLayout.CENTER);
        JPanel abajoCentro = new JPanel(new BorderLayout(5, 5));
        abajoCentro.add(new JLabel("Imagen del anuncio (opcional):"), BorderLayout.NORTH);
        abajoCentro.add(selectorImagen, BorderLayout.CENTER);
        centro.add(abajoCentro, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        JButton botonGuardar = new JButton("Guardar cambios");
        JButton botonEliminar = new JButton("Eliminar anuncio");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(botonGuardar);
        botones.add(botonEliminar);
        add(botones, BorderLayout.SOUTH);

        lista.addListSelectionListener(e -> mostrarSeleccionado());
        botonGuardar.addActionListener(e -> guardarCambios());
        botonEliminar.addActionListener(e -> eliminarSeleccionado());

        cargarLista();
    }

    private void cargarLista() {
        modelo.clear();
        for (Anuncio a : club.getAnuncios()) {
            modelo.addElement(a.getTitulo() + "  |  " + a.getFecha());
        }
    }

    private void mostrarSeleccionado() {
        int i = lista.getSelectedIndex();
        if (i < 0) {
            campoTitulo.setText("");
            campoMensaje.setText("");
            selectorImagen.setImagen(null);
            return;
        }
        Anuncio a = club.getAnuncios().get(i);
        campoTitulo.setText(a.getTitulo());
        campoMensaje.setText(a.getMensaje());
        campoMensaje.setCaretPosition(0);
        selectorImagen.setImagen(extras.getImagenAnuncio(club, i));
    }

    private void guardarCambios() {
        int i = lista.getSelectedIndex();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un anuncio de la lista.");
            return;
        }
        Anuncio a = club.getAnuncios().get(i);
        try {
            controlador.editarAnuncio(lider, a, campoTitulo.getText(), campoMensaje.getText());
            extras.setImagenAnuncio(club, i, selectorImagen.getImagen());
            extras.guardar();
            cargarLista();
            lista.setSelectedIndex(i);
            JOptionPane.showMessageDialog(this, "Anuncio actualizado.");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionado() {
        int i = lista.getSelectedIndex();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un anuncio de la lista.");
            return;
        }
        Anuncio a = club.getAnuncios().get(i);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar el anuncio \"" + a.getTitulo() + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controlador.eliminarAnuncio(lider, a);
            extras.eliminarImagenAnuncio(club, i);
            extras.guardar();
            cargarLista();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
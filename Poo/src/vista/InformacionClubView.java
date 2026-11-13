package vista;

import controlador.ClubController;
import modelo.Club;
import modelo.Usuario;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

/** Edición de información y medios; los cambios se aplican al presionar Guardar. */
public class InformacionClubView extends JPanel {
    public InformacionClubView(Usuario usuario, Club club, ClubController controlador, Runnable volver) {
        setLayout(new BorderLayout(12, 12));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel titulo = new JLabel("Información de " + club.getNombre());
        titulo.setFont(Estilos.SUBTITULO);
        add(titulo, BorderLayout.NORTH);

        JTextField ubicacion = new JTextField(club.getUbicacion());
        ubicacion.setName("ubicacionClub");
        JTextArea descripcion = new JTextArea(club.getDescripcion(), 5, 32);
        descripcion.setName("descripcionClub");
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        JTextArea contactos = new JTextArea(club.getContactos(), 3, 32);
        contactos.setName("contactosClub");
        contactos.setLineWrap(true);
        contactos.setWrapStyleWord(true);
        descripcion.setFont(Estilos.TEXTO);
        contactos.setFont(Estilos.TEXTO);
        JPanel campos = new JPanel();
        campos.setLayout(new BoxLayout(campos, BoxLayout.Y_AXIS));
        campos.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        campos.add(new JLabel("Ubicación (máximo 150 caracteres)"));
        ubicacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        campos.add(ubicacion);
        campos.add(Box.createVerticalStrut(10));
        campos.add(new JLabel("Descripción (máximo 2000 caracteres)"));
        campos.add(new JScrollPane(descripcion));
        campos.add(Box.createVerticalStrut(10));
        campos.add(new JLabel("Contactos (uno por línea; máximo 500 caracteres)"));
        campos.add(new JScrollPane(contactos));

        SelectorImagen logo = new SelectorImagen(100, 100, "Elegir logo");
        logo.setName("selectorLogoClub");
        logo.setImagen(club.getLogo());
        JPanel panelLogo = new JPanel(new BorderLayout(8, 8));
        panelLogo.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        panelLogo.add(new JLabel("Logo visible para estudiantes y líderes. PNG, JPG o GIF de hasta 3 MB."), BorderLayout.NORTH);
        panelLogo.add(logo, BorderLayout.CENTER);

        ArrayList<byte[]> fotos = club.getFotos();
        DefaultListModel<byte[]> modelo = new DefaultListModel<>();
        for (byte[] foto : fotos) modelo.addElement(foto);
        JList<byte[]> lista = new JList<>(modelo);
        lista.setName("listaFotosClub");
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setCellRenderer((l, dato, indice, seleccionado, foco) -> {
            JLabel fila = new JLabel("Foto " + (indice + 1), Imagenes.ajustar(dato, 80, 52), SwingConstants.LEFT);
            fila.setOpaque(true);
            fila.setBackground(seleccionado ? Estilos.VERDE_MUY_CLARO : Color.WHITE);
            fila.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return fila;
        });
        JLabel vista = new JLabel("Selecciona una foto", SwingConstants.CENTER);
        vista.setName("vistaPreviaFotoClub");
        lista.addListSelectionListener(e -> {
            byte[] seleccion = lista.getSelectedValue();
            vista.setIcon(Imagenes.ajustar(seleccion, 340, 220));
            vista.setText(seleccion == null ? "Selecciona una foto" : "");
        });
        JButton agregar = new JButton("Añadir foto");
        agregar.setName("agregarFotoClub");
        JButton quitar = new JButton("Quitar seleccionada");
        quitar.setName("quitarFotoClub");
        JLabel cantidad = new JLabel(fotos.size() + " / 12 fotos · Hasta 3 MB por imagen");
        agregar.addActionListener(e -> {
            if (fotos.size() >= 12) {
                JOptionPane.showMessageDialog(this, "Puedes publicar hasta 12 fotos por club.");
                return;
            }
            byte[] imagen = Imagenes.elegirImagen(this);
            if (imagen != null) {
                fotos.add(imagen);
                modelo.addElement(imagen);
                lista.setSelectedIndex(fotos.size() - 1);
                cantidad.setText(fotos.size() + " / 12 fotos · Hasta 3 MB por imagen");
            }
        });
        quitar.addActionListener(e -> {
            int i = lista.getSelectedIndex();
            if (i < 0) return;
            fotos.remove(i);
            modelo.remove(i);
            if (!fotos.isEmpty()) lista.setSelectedIndex(Math.min(i, fotos.size() - 1));
            cantidad.setText(fotos.size() + " / 12 fotos · Hasta 3 MB por imagen");
        });
        Estilos.estilizarBoton(agregar);
        Estilos.estilizarBotonSecundario(quitar);
        JPanel accionesFotos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accionesFotos.add(agregar);
        accionesFotos.add(quitar);
        JPanel panelFotos = new JPanel(new BorderLayout(10, 10));
        panelFotos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelFotos.add(accionesFotos, BorderLayout.NORTH);
        JScrollPane desplazamientoLista = new JScrollPane(lista);
        desplazamientoLista.setPreferredSize(new Dimension(170, 160));
        panelFotos.add(desplazamientoLista, BorderLayout.WEST);
        panelFotos.add(new JScrollPane(vista), BorderLayout.CENTER);
        panelFotos.add(cantidad, BorderLayout.SOUTH);
        if (!fotos.isEmpty()) lista.setSelectedIndex(0);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setName("pestanasEditarInformacion");
        pestanas.addTab("Información y contactos", campos);
        pestanas.addTab("Logo", panelLogo);
        pestanas.addTab("Fotos del club", panelFotos);
        add(pestanas, BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton guardar = new JButton("Guardar cambios");
        guardar.setName("guardarInformacionClub");
        JButton cancelar = new JButton("Volver sin guardar");
        cancelar.setName("cancelarInformacionClub");
        Estilos.estilizarBoton(guardar);
        Estilos.estilizarBotonSecundario(cancelar);
        botones.add(cancelar);
        botones.add(guardar);
        add(botones, BorderLayout.SOUTH);
        guardar.addActionListener(e -> {
            try {
                controlador.actualizarInformacionClub(usuario, club, descripcion.getText(), ubicacion.getText(),
                        contactos.getText(), logo.getImagen(), fotos);
                volver.run();
            } catch (RuntimeException ex) { VentanaPrincipal.mostrarError(this, ex); }
        });
        cancelar.addActionListener(e -> volver.run());
    }
}

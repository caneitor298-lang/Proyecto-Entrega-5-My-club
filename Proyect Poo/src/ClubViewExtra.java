import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ClubViewExtra extends JPanel {
    private final ExtrasClub extras = ExtrasClub.obtener();

    public ClubViewExtra(Usuario usuario, Club club, ClubController controlador, Runnable volver,
                         Runnable refrescar, Runnable editarAnuncios) {
        setLayout(new BorderLayout(12, 12));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel cabecera = new JPanel(new BorderLayout(14, 0));
        if (extras.getLogo(club) != null) {
            cabecera.add(new JLabel(ImagenesExtra.logo(extras.getLogo(club), 64)), BorderLayout.WEST);
        }

        JPanel textosCabecera = new JPanel(new GridLayout(0, 1, 2, 2));
        textosCabecera.setOpaque(false);
        JLabel titulo = new JLabel(club.getNombre());
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.VERDE_OSCURO);
        JLabel detalle = new JLabel("Categoría: " + club.getCategoria() + "   ·   Ubicación: " + club.getUbicacion());
        detalle.setFont(Estilos.TEXTO);
        textosCabecera.add(titulo);
        textosCabecera.add(detalle);
        cabecera.add(textosCabecera, BorderLayout.CENTER);

        JButton regresar = new JButton("Volver");
        regresar.addActionListener(e -> volver.run());
        cabecera.add(regresar, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        JTextArea descripcion = new JTextArea(club.getDescripcion());
        descripcion.setEditable(false);
        descripcion.setFont(Estilos.TEXTO);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setOpaque(false);
        JScrollPane scrollDescripcion = new JScrollPane(descripcion);
        scrollDescripcion.setBorder(BorderFactory.createEmptyBorder());
        scrollDescripcion.setOpaque(false);
        scrollDescripcion.getViewport().setOpaque(false);
        scrollDescripcion.setPreferredSize(new Dimension(100, 44));

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(Estilos.BOTON);
        pestanas.addTab("Anuncios", crearPestanaAnuncios(club));
        pestanas.addTab("Horario", crearPestanaHorario(club));
        pestanas.addTab("Contactos", crearPestanaContactos(club));

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setOpaque(false);
        centro.add(scrollDescripcion, BorderLayout.NORTH);
        centro.add(pestanas, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new BorderLayout(12, 0));
        if (usuario instanceof Estudiante) {
            Estudiante estudiante = (Estudiante) usuario;
            boolean pertenece = estudiante.getClubes().contains(club);
            JButton membresia = new JButton(pertenece ? "Salir del club" : "Unirme al club");
            Estilos.estilizarBoton(membresia);
            membresia.addActionListener(e -> {
                if (pertenece && JOptionPane.showConfirmDialog(this, "¿Quieres salir de " + club.getNombre() + "?",
                        "Salir del club", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                try {
                    if (pertenece) controlador.eliminarEstudianteDeClub(estudiante, club);
                    else controlador.unirEstudianteAClub(estudiante, club);
                    refrescar.run();
                } catch (Exception ex) { VentanaPrincipalExtra.mostrarError(this, ex); }
            });
            JLabel estadoMembresia = new JLabel(pertenece ? "Ya eres miembro de este club." : "Puedes unirte cuando quieras.");
            estadoMembresia.setFont(Estilos.TEXTO);
            acciones.add(estadoMembresia, BorderLayout.WEST);
            acciones.add(membresia, BorderLayout.EAST);
        } else if (usuario instanceof Lider && ((Lider) usuario).getClubAdministrado() == club) {
            JButton publicar = new JButton("Publicar anuncio");
            publicar.addActionListener(e -> publicarAnuncio(usuario, club, controlador, refrescar));
            acciones.add(publicar);
            JButton horario = new JButton("Cambiar horario");
            horario.addActionListener(e -> cambiarHorario(usuario, club, controlador, refrescar));
            acciones.add(horario);
            JButton miembros = new JButton("Ver miembros");
            miembros.addActionListener(e -> mostrarMiembros(usuario, club, controlador));
            acciones.add(miembros);
            JButton anuncios = new JButton("Editar anuncios");
            anuncios.addActionListener(e -> editarAnuncios.run());
            acciones.add(anuncios);
            JButton logo = new JButton("Cambiar logo");
            logo.addActionListener(e -> cambiarLogo(club, refrescar));
            acciones.add(logo);
            JButton contactos = new JButton("Editar contactos");
            contactos.addActionListener(e -> editarContactos(club, refrescar));
            acciones.add(contactos);
            acciones.setLayout(new GridLayout(0, 3, 8, 8));
        } else acciones.add(new JLabel("Solo el líder de este club puede administrarlo."));
        add(acciones, BorderLayout.SOUTH);
    }

    private JScrollPane crearPestanaAnuncios(Club club) {
        JTextPane panel = new JTextPane();
        panel.setEditable(false);
        panel.setMargin(new Insets(15, 15, 15, 15));

        SimpleAttributeSet estiloTitulo = new SimpleAttributeSet();
        StyleConstants.setFontSize(estiloTitulo, 17);
        StyleConstants.setBold(estiloTitulo, true);
        StyleConstants.setForeground(estiloTitulo, Estilos.VERDE_OSCURO);

        SimpleAttributeSet estiloFecha = new SimpleAttributeSet();
        StyleConstants.setFontSize(estiloFecha, 12);
        StyleConstants.setForeground(estiloFecha, Estilos.GRIS_TEXTO);

        SimpleAttributeSet estiloTexto = new SimpleAttributeSet();
        StyleConstants.setFontSize(estiloTexto, 14);

        StyledDocument documento = panel.getStyledDocument();
        try {
            if (club.getAnuncios().isEmpty()) {
                documento.insertString(documento.getLength(), "Todavía no hay anuncios publicados.", estiloTexto);
            }
            for (int i = club.getAnuncios().size() - 1; i >= 0; i--) {
                Anuncio anuncio = club.getAnuncios().get(i);
                documento.insertString(documento.getLength(), anuncio.getTitulo() + "\n", estiloTitulo);
                documento.insertString(documento.getLength(), anuncio.getFecha() + "\n", estiloFecha);
                documento.insertString(documento.getLength(), anuncio.getMensaje() + "\n", estiloTexto);
                byte[] datos = extras.getImagenAnuncio(club, i);
                if (datos != null) {
                    ImageIcon imagen = ImagenesExtra.ajustar(datos, 560, 380);
                    if (imagen != null) {
                        SimpleAttributeSet estiloImagen = new SimpleAttributeSet();
                        StyleConstants.setIcon(estiloImagen, imagen);
                        documento.insertString(documento.getLength(), " ", estiloImagen);
                        documento.insertString(documento.getLength(), "\n", estiloTexto);
                    }
                }
                documento.insertString(documento.getLength(), "\n", estiloTexto);
            }
        } catch (BadLocationException ex) {
            panel.setText("No se pudieron mostrar los anuncios.");
        }
        panel.setCaretPosition(0);
        return new JScrollPane(panel);
    }

    private JScrollPane crearPestanaHorario(Club club) {
        String texto = "HORARIO\n\n"
                + "•  " + club.getHorario() + "\n\n\n"
                + "UBICACIÓN\n\n"
                + "•  " + club.getUbicacion();
        return crearPestanaTexto(texto);
    }

    private JScrollPane crearPestanaContactos(Club club) {
        String texto = "CONTACTOS\n\n";
        String[] lineas = extras.getContactos(club).split("\\R");
        boolean hayContactos = false;
        for (String linea : lineas) {
            if (!linea.trim().isEmpty()) {
                texto += "•  " + linea.trim() + "\n";
                hayContactos = true;
            }
        }
        if (!hayContactos) {
            texto += "Todavía no hay contactos publicados.";
        }
        return crearPestanaTexto(texto);
    }

    private JScrollPane crearPestanaTexto(String texto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(Estilos.TEXTO);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(15, 15, 15, 15));
        area.setCaretPosition(0);
        return new JScrollPane(area);
    }

    private void mostrarMiembros(Usuario usuario, Club club, ClubController controlador) {
        ArrayList<Estudiante> lista;
        try {
            lista = controlador.obtenerMiembros(usuario, club);
        } catch (Exception ex) {
            VentanaPrincipalExtra.mostrarError(this, ex);
            return;
        }
        DefaultTableModel modelo = new DefaultTableModel(new String[] {"Nombre", "Correo", "Carrera"}, 0) {
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        for (Estudiante e : lista) {
            modelo.addRow(new Object[] {e.getNombre(), e.getCorreo(), e.getCarrera()});
        }
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(520, 260));
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JLabel total = new JLabel("Total de miembros: " + lista.size());
        total.setFont(Estilos.SUBTITULO);
        panel.add(total, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        if (lista.isEmpty()) {
            panel.add(new JLabel("Todavía no hay estudiantes inscritos en este club."), BorderLayout.SOUTH);
        }
        JOptionPane.showMessageDialog(this, panel, "Miembros de " + club.getNombre(), JOptionPane.PLAIN_MESSAGE);
    }

    private void publicarAnuncio(Usuario usuario, Club club, ClubController controlador, Runnable refrescar) {
        JTextField titulo = new JTextField();
        JTextArea mensaje = new JTextArea(7, 34);
        mensaje.setLineWrap(true);
        mensaje.setWrapStyleWord(true);
        SelectorImagenExtra selector = new SelectorImagenExtra(120, 90, "Adjuntar imagen");

        JPanel arriba = new JPanel(new GridLayout(0, 1, 4, 4));
        arriba.add(new JLabel("Título (máximo 100 caracteres)"));
        arriba.add(titulo);
        arriba.add(new JLabel("Mensaje (máximo 2000 caracteres)"));
        JPanel abajo = new JPanel(new BorderLayout(4, 4));
        abajo.add(new JLabel("Imagen del anuncio (opcional)"), BorderLayout.NORTH);
        abajo.add(selector, BorderLayout.CENTER);
        JPanel formulario = new JPanel(new BorderLayout(8, 8));
        formulario.add(arriba, BorderLayout.NORTH);
        formulario.add(new JScrollPane(mensaje), BorderLayout.CENTER);
        formulario.add(abajo, BorderLayout.SOUTH);

        while (confirmar(formulario, "Publicar anuncio", "Publicar")) {
            try {
                controlador.publicarAnuncio(usuario, club, titulo.getText(), mensaje.getText());
                extras.setImagenAnuncio(club, club.getAnuncios().size() - 1, selector.getImagen());
                extras.guardar();
                refrescar.run();
                return;
            } catch (Exception ex) { VentanaPrincipalExtra.mostrarError(this, ex); }
        }
    }

    private void cambiarLogo(Club club, Runnable refrescar) {
        SelectorImagenExtra selector = new SelectorImagenExtra(90, 90, "Elegir logo");
        selector.setImagen(extras.getLogo(club));
        JPanel panel = new JPanel(new BorderLayout(4, 8));
        panel.add(new JLabel("Logo del club"), BorderLayout.NORTH);
        panel.add(selector, BorderLayout.CENTER);
        if (!confirmar(panel, "Cambiar logo", "Guardar")) return;
        extras.setLogo(club, selector.getImagen());
        extras.guardar();
        refrescar.run();
    }

    private void editarContactos(Club club, Runnable refrescar) {
        JTextArea contactos = new JTextArea(extras.getContactos(club), 6, 34);
        contactos.setLineWrap(true);
        contactos.setWrapStyleWord(true);
        JPanel panel = new JPanel(new BorderLayout(4, 8));
        panel.add(new JLabel("Contactos, uno por línea (máximo 500 caracteres)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(contactos), BorderLayout.CENTER);
        while (confirmar(panel, "Editar contactos", "Guardar")) {
            String texto = contactos.getText().trim();
            if (texto.length() > 500) {
                JOptionPane.showMessageDialog(this, "Los contactos admiten hasta 500 caracteres.",
                        "Revisa la operación", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            extras.setContactos(club, texto);
            extras.guardar();
            refrescar.run();
            return;
        }
    }

    private boolean confirmar(JPanel contenido, String titulo, String textoAceptar) {
        Object[] botones = {textoAceptar, "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(this, contenido, titulo, JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, botones, textoAceptar);
        return opcion == 0;
    }

    private void cambiarHorario(Usuario usuario, Club club, ClubController controlador, Runnable refrescar) {
        String horario = club.getHorario();
        while (true) {
            horario = (String) JOptionPane.showInputDialog(this,
                    "Escribe el día y las horas de reunión (máximo 150 caracteres):",
                    "Cambiar horario", JOptionPane.PLAIN_MESSAGE, null, null, horario);
            if (horario == null) return;
            try {
                controlador.cambiarHorarioClub(usuario, club, horario);
                refrescar.run();
                return;
            } catch (Exception ex) { VentanaPrincipalExtra.mostrarError(this, ex); }
        }
    }
}
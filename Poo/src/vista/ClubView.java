package vista;

import modelo.*;
import controlador.*;
import persistencia.DatosAplicacion;
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

public class ClubView extends JPanel {

    public ClubView(Usuario usuario, Club club, ClubController controlador, Runnable volver,
                         Runnable refrescar, Runnable editarAnuncios, Runnable editarInformacion) {
        setLayout(new BorderLayout(12, 12));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel cabecera = new JPanel(new BorderLayout(14, 0));
        JLabel logoClub = new JLabel(Imagenes.logo(club, 64));
        logoClub.setName("logoClub");
        cabecera.add(logoClub, BorderLayout.WEST);

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

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(Estilos.BOTON);
        pestanas.setName("pestanasClub");
        pestanas.addTab("Información", crearPestanaInformacion(club));
        pestanas.addTab("Anuncios", crearPestanaAnuncios(club));
        pestanas.addTab("Horario", crearPestanaHorario(club));
        pestanas.addTab("Contactos", crearPestanaContactos(club));

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setOpaque(false);
        centro.add(pestanas, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new BorderLayout(12, 0));
        if (usuario.getRango().puedeUnirseAClubes() && usuario instanceof Estudiante) {
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
                } catch (Exception ex) { VentanaPrincipal.mostrarError(this, ex); }
            });
            JLabel estadoMembresia = new JLabel(pertenece ? "Ya eres miembro de este club." : "Puedes unirte cuando quieras.");
            estadoMembresia.setFont(Estilos.TEXTO);
            acciones.add(estadoMembresia, BorderLayout.WEST);
            acciones.add(membresia, BorderLayout.EAST);
        } else if (usuario.getRango().puedeAdministrarClub() && usuario instanceof Lider && ((Lider) usuario).getClubAdministrado() == club) {
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
            JButton informacion = new JButton("Editar información y fotos");
            informacion.setName("editarInformacionClub");
            informacion.addActionListener(e -> editarInformacion.run());
            acciones.add(informacion);
            acciones.setLayout(new GridLayout(0, 3, 8, 8));
        } else acciones.add(new JLabel("Solo el líder de este club puede administrarlo."));
        for (java.awt.Component componente : acciones.getComponents()) {
            if (componente instanceof JButton) Estilos.estilizarBoton((JButton) componente);
        }
        Estilos.estilizarBotonSecundario(regresar);
        add(acciones, BorderLayout.SOUTH);
    }

    private JPanel crearPestanaInformacion(Club club) {
        JPanel informacion = new JPanel(new BorderLayout(8, 8));
        informacion.setName("informacionClub");
        informacion.setBackground(Estilos.BLANCO);
        informacion.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JTextArea texto = new JTextArea(club.getDescripcion(), 3, 30);
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(Estilos.TEXTO);
        texto.setMargin(new Insets(6, 6, 6, 6));
        JScrollPane descripcion = new JScrollPane(texto);
        descripcion.setPreferredSize(new Dimension(400, 72));
        informacion.add(descripcion, BorderLayout.NORTH);
        JPanel galeria = new JPanel();
        galeria.setLayout(new javax.swing.BoxLayout(galeria, javax.swing.BoxLayout.Y_AXIS));
        galeria.setBackground(Estilos.BLANCO);
        JLabel titulo = new JLabel("Fotos del club");
        titulo.setFont(Estilos.SUBTITULO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        galeria.add(titulo);
        ArrayList<byte[]> fotos = club.getFotos();
        if (fotos.isEmpty()) galeria.add(new JLabel("Todavía no hay fotos publicadas."));
        for (int i = 0; i < fotos.size(); i++) {
            ImageIcon imagen = Imagenes.ajustar(fotos.get(i), 460, 300);
            JLabel foto = new JLabel(imagen);
            foto.setName("fotoClub" + i);
            foto.setAlignmentX(LEFT_ALIGNMENT);
            foto.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            galeria.add(foto);
        }
        informacion.add(new JScrollPane(galeria), BorderLayout.CENTER);
        return informacion;
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
                byte[] datos = anuncio.getImagen();
                if (datos != null) {
                    ImageIcon imagen = Imagenes.ajustar(datos, 560, 380);
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
        String[] lineas = club.getContactos().split("\\R");
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
            VentanaPrincipal.mostrarError(this, ex);
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
        SelectorImagen selector = new SelectorImagen(120, 90, "Adjuntar imagen");

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
                controlador.publicarAnuncio(usuario, club, titulo.getText(), mensaje.getText(), selector.getImagen());
                refrescar.run();
                return;
            } catch (Exception ex) { VentanaPrincipal.mostrarError(this, ex); }
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
            } catch (Exception ex) { VentanaPrincipal.mostrarError(this, ex); }
        }
    }
}
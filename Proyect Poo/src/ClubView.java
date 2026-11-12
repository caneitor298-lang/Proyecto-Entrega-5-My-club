import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ClubView extends JPanel {
    public ClubView(Usuario usuario, Club club, ClubController controlador, Runnable volver, Runnable refrescar,
                    Runnable editarAnuncios, Runnable editarInformacion) {
        setLayout(new BorderLayout(12, 12));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        JPanel cabecera = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel(club.getNombre());
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.VERDE_OSCURO);
        cabecera.add(titulo, BorderLayout.CENTER);
        JButton regresar = new JButton("Volver");
        Estilos.estilizarBotonSecundario(regresar);
        regresar.addActionListener(e -> volver.run());
        cabecera.add(regresar, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);
        JTextArea informacion = new JTextArea();
        informacion.setEditable(false);
        informacion.setFont(Estilos.TEXTO);
        informacion.setLineWrap(true);
        informacion.setWrapStyleWord(true);
        informacion.setMargin(new Insets(15, 15, 15, 15));
        informacion.append("Categoría: " + club.getCategoria() + "\nUbicación: " + club.getUbicacion()
                + "\nHorario: " + club.getHorario() + "\n\n" + club.getDescripcion() + "\n\nANUNCIOS\n\n");
        if (club.getAnuncios().isEmpty()) informacion.append("Todavía no hay anuncios publicados.");
        for (int i = club.getAnuncios().size() - 1; i >= 0; i--) {
            Anuncio anuncio = club.getAnuncios().get(i);
            informacion.append(anuncio.getTitulo() + "  |  " + anuncio.getFecha() + "\n" + anuncio.getMensaje() + "\n\n");
        }
        informacion.setCaretPosition(0);
        add(new JScrollPane(informacion), BorderLayout.CENTER);
        JPanel acciones = new JPanel(new BorderLayout(12, 0));
        if (usuario.getRango().puedeUnirseAClubes()) {
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
        } else if (usuario.getRango().puedeAdministrarClub() && usuario instanceof Lider
                && ((Lider) usuario).getClubAdministrado() == club) {
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
            JButton informacionClub = new JButton("Editar información");
            informacionClub.addActionListener(e -> editarInformacion.run());
            acciones.add(informacionClub);
            
            acciones.setLayout(new GridLayout(0, 3, 8, 8));
        } else acciones.add(new JLabel(usuario.getRango().puedeRevisarSolicitudes()
                ? "Como administrador puedes consultar el club; la gestión corresponde a su líder."
                : "Solo el líder de este club puede administrarlo."));
        add(acciones, BorderLayout.SOUTH);
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
        JPanel formulario = new JPanel(new BorderLayout(8, 8));
        JPanel arriba = new JPanel(new GridLayout(0, 1, 4, 4));
        arriba.add(new JLabel("Título (máximo 100 caracteres)"));
        arriba.add(titulo);
        arriba.add(new JLabel("Mensaje (máximo 2000 caracteres)"));
        formulario.add(arriba, BorderLayout.NORTH);
        formulario.add(new JScrollPane(mensaje), BorderLayout.CENTER);
        while (JOptionPane.showConfirmDialog(this, formulario, "Publicar anuncio",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                controlador.publicarAnuncio(usuario, club, titulo.getText(), mensaje.getText());
                refrescar.run();
                return;
            } catch (Exception ex) { VentanaPrincipal.mostrarError(this, ex); }
        }
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

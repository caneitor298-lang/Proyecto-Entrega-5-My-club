package vista;

import modelo.*;
import controlador.*;
import persistencia.DatosAplicacion;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class MisClubsView extends JPanel {

    public MisClubsView(Estudiante estudiante, ClubController controlador,
                             Consumer<Club> abrirClub, Runnable refrescar) {
        setLayout(new BorderLayout(12, 12));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JLabel titulo = new JLabel("Mis clubes");
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.AZUL_OSCURO);
        cabecera.add(titulo, BorderLayout.WEST);
        JLabel contador = new JLabel(estudiante.getClubes().size() + " club(es)");
        contador.setFont(Estilos.TEXTO);
        cabecera.add(contador, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        DefaultTableModel modelo = new DefaultTableModel(new String[] {"Club", "Horario"}, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (Club club : estudiante.getClubes()) {
            modelo.addRow(new Object[] {club, club.getHorario()});
        }

        JTable tabla = new JTable(modelo);
        tabla.getColumnModel().getColumn(0).setCellRenderer(new RenderizadorClub());
        tabla.setName("tablaMisClubes");
        tabla.setRowHeight(40);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setFont(Estilos.TEXTO);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(Estilos.AZUL_CLARO);
        tabla.setSelectionForeground(Estilos.AZUL_OSCURO);
        tabla.getTableHeader().setFont(Estilos.BOTON);
        tabla.getTableHeader().setBackground(Estilos.AZUL_OSCURO);
        tabla.getTableHeader().setForeground(Estilos.BLANCO);
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Estilos.BORDE, 1));
        scroll.getViewport().setBackground(Estilos.BLANCO);
        add(scroll, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(12, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JLabel mensaje = new JLabel(estudiante.getClubes().isEmpty()
                ? "Todavía no perteneces a ningún club. ¡Explora y únete a uno!"
                : "Selecciona un club de la lista para verlo o salir de él.");
        mensaje.setFont(Estilos.TEXTO);
        pie.add(mensaje, BorderLayout.NORTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        JButton ver = new JButton("Ver club");
        Estilos.estilizarBoton(ver);
        ver.setPreferredSize(new Dimension(120, 38));
        ver.setEnabled(false);
        JButton salir = new JButton("Salir del club");
        Estilos.estilizarBotonPeligro(salir);
        salir.setPreferredSize(new Dimension(140, 38));
        salir.setEnabled(false);
        botones.add(ver);
        botones.add(salir);
        pie.add(botones, BorderLayout.SOUTH);
        add(pie, BorderLayout.SOUTH);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            boolean haySeleccion = tabla.getSelectedRow() >= 0;
            ver.setEnabled(haySeleccion);
            salir.setEnabled(haySeleccion);
        });

        ver.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                abrirClub.accept(estudiante.getClubes().get(fila));
            }
        });

        salir.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) return;
            Club club = estudiante.getClubes().get(fila);
            int opcion = JOptionPane.showConfirmDialog(this, "¿Quieres salir de \"" + club.getNombre() + "\"?",
                    "Salir del club", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) return;
            try {
                controlador.eliminarEstudianteDeClub(estudiante, club);
                refrescar.run();
            } catch (Exception ex) {
                VentanaPrincipal.mostrarError(this, ex);
            }
        });

        if (!estudiante.getClubes().isEmpty()) {
            tabla.setRowSelectionInterval(0, 0);
        }
    }
}
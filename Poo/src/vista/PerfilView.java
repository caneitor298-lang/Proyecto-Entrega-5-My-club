package vista;

import persistencia.DatosAplicacion;
import modelo.Estudiante;
import controlador.UsuarioController;
import java.awt.*;
import javax.swing.*;

public class PerfilView extends JPanel {

    public PerfilView(Estudiante estudiante,
                      UsuarioController controlador,
                      DatosAplicacion datos,
                      Runnable volver) {

        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Estilos.BLANCO);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE, 1),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;

        int fila = 0;

        JLabel titulo = new JLabel("Mi perfil");
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.VERDE_OSCURO);
        c.gridy = fila++;
        c.insets = new Insets(0, 0, 18, 0);
        form.add(titulo, c);

        JTextField nombre = crearCampoTexto(estudiante.getNombre());
        JTextField carrera = crearCampoTexto(estudiante.getCarrera());
        JPasswordField actual = crearCampoPassword();
        JPasswordField nueva = crearCampoPassword();
        JPasswordField confirmar = crearCampoPassword();

        fila = agregarCampo(form, c, fila, "Nombre completo", nombre);
        fila = agregarCampo(form, c, fila, "Carrera", carrera);

        JSeparator sep = new JSeparator();
        sep.setForeground(Estilos.BORDE);
        c.gridy = fila++;
        c.insets = new Insets(6, 0, 12, 0);
        form.add(sep, c);

        JLabel secPass = new JLabel("Cambiar contraseña");
        secPass.setFont(Estilos.SUBTITULO);
        secPass.setForeground(Estilos.VERDE_OSCURO);
        c.gridy = fila++;
        c.insets = new Insets(0, 0, 12, 0);
        form.add(secPass, c);

        fila = agregarCampo(form, c, fila, "Contraseña actual", actual);
        fila = agregarCampo(form, c, fila, "Nueva contraseña", nueva);
        fila = agregarCampo(form, c, fila, "Confirmar nueva contraseña", confirmar);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);

        JButton guardar = new JButton("Guardar cambios");
        Estilos.estilizarBoton(guardar);
        guardar.setPreferredSize(new Dimension(150, 36));

        JButton cancelar = new JButton("Volver");
        Estilos.estilizarBotonSecundario(cancelar);
        cancelar.setPreferredSize(new Dimension(100, 36));

        botones.add(guardar);
        botones.add(cancelar);

        c.gridy = fila++;
        c.insets = new Insets(12, 0, 0, 0);
        form.add(botones, c);

        guardar.addActionListener(e -> {
            try {
                controlador.actualizarPerfil(
                        estudiante,
                        nombre.getText(),
                        carrera.getText(),
                        new String(actual.getPassword()),
                        new String(nueva.getPassword()),
                        new String(confirmar.getPassword())
                );
                JOptionPane.showMessageDialog(this, "Perfil actualizado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                volver.run();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelar.addActionListener(e -> volver.run());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        wrapper.add(form, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilos.FONDO);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    private int agregarCampo(JPanel form, GridBagConstraints c, int fila, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Estilos.TEXTO_PEQUENO);
        lbl.setForeground(Estilos.GRIS_TEXTO);
        c.gridy = fila++;
        c.insets = new Insets(0, 0, 3, 0);
        form.add(lbl, c);

        c.gridy = fila++;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(campo, c);
        return fila;
    }

    private JTextField crearCampoTexto(String valor) {
        JTextField tf = new JTextField(valor);
        Estilos.estilizarCampo(tf);
        tf.setPreferredSize(new Dimension(100, 34));
        tf.setMinimumSize(new Dimension(80, 34));
        return tf;
    }

    private JPasswordField crearCampoPassword() {
        JPasswordField pf = new JPasswordField();
        Estilos.estilizarCampo(pf);
        pf.setPreferredSize(new Dimension(100, 34));
        pf.setMinimumSize(new Dimension(80, 34));
        return pf;
    }
}

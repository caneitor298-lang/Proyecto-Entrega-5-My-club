package vista;

import modelo.Estudiante;
import modelo.Usuario;
import controlador.UsuarioController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class VistaRegistro extends FondoDeportesArte {
    private static final long serialVersionUID = 1L;
    public VistaRegistro(UsuarioController controlador, Consumer<Usuario> alRegistrar, Runnable volver) {
        setLayout(new BorderLayout());

        JPanel tarjeta = new JPanel(null);
        tarjeta.setPreferredSize(new Dimension(448, 520));
        tarjeta.setBackground(EstilosAcceso.TARJETA);

        JLabel titulo = new JLabel("Crear cuenta", SwingConstants.CENTER);
        titulo.setFont(EstilosAcceso.LOGO);
        titulo.setForeground(Color.BLACK);
        titulo.setBounds(0, 20, 448, 45);
        tarjeta.add(titulo);

        JLabel textoNombre = new JLabel("Nombre completo");
        textoNombre.setFont(EstilosAcceso.ETIQUETA);
        textoNombre.setBounds(64, 75, 320, 20);
        tarjeta.add(textoNombre);

        JTextField nombre = new JTextField();
        nombre.setName("nombreRegistro");
        EstilosAcceso.estilizarCampo(nombre);
        nombre.setBounds(64, 98, 320, 18);
        tarjeta.add(nombre);

        JLabel textoCorreo = new JLabel("Correo institucional");
        textoCorreo.setFont(EstilosAcceso.ETIQUETA);
        textoCorreo.setBounds(64, 137, 320, 20);
        tarjeta.add(textoCorreo);

        JTextField correo = new JTextField();
        correo.setName("correoRegistro");
        EstilosAcceso.estilizarCampo(correo);
        correo.setBounds(64, 160, 320, 18);
        tarjeta.add(correo);

        JLabel textoCarrera = new JLabel("Carrera");
        textoCarrera.setFont(EstilosAcceso.ETIQUETA);
        textoCarrera.setBounds(64, 199, 320, 20);
        tarjeta.add(textoCarrera);

        JTextField carrera = new JTextField();
        carrera.setName("carreraRegistro");
        EstilosAcceso.estilizarCampo(carrera);
        carrera.setBounds(64, 222, 320, 18);
        tarjeta.add(carrera);

        JLabel textoContrasena = new JLabel("Contraseña (mínimo 8 caracteres)");
        textoContrasena.setFont(EstilosAcceso.ETIQUETA);
        textoContrasena.setBounds(64, 261, 320, 20);
        tarjeta.add(textoContrasena);

        JPasswordField contrasena = new JPasswordField();
        contrasena.setName("contrasenaRegistro");
        EstilosAcceso.estilizarCampo(contrasena);
        contrasena.setBounds(64, 284, 320, 18);
        tarjeta.add(contrasena);

        JLabel textoConfirmar = new JLabel("Confirmar contraseña");
        textoConfirmar.setFont(EstilosAcceso.ETIQUETA);
        textoConfirmar.setBounds(64, 323, 320, 20);
        tarjeta.add(textoConfirmar);

        JPasswordField confirmar = new JPasswordField();
        confirmar.setName("confirmarRegistro");
        EstilosAcceso.estilizarCampo(confirmar);
        confirmar.setBounds(64, 346, 320, 18);
        tarjeta.add(confirmar);

        JButton crear = new JButton("Crear cuenta");
        crear.setName("crearEstudiante");
        EstilosAcceso.estilizarBotonVerde(crear);
        crear.setBounds(64, 388, 320, 32);
        tarjeta.add(crear);

        JButton regresar = new JButton("Volver al inicio de sesión");
        regresar.setName("volverRegistro");
        EstilosAcceso.estilizarBotonClaro(regresar);
        regresar.setBounds(64, 434, 320, 33);
        tarjeta.add(regresar);

        JLabel estado = new JLabel(" ");
        estado.setName("estadoRegistro");
        estado.setFont(EstilosAcceso.ETIQUETA);
        estado.setForeground(Color.RED);
        estado.setBounds(64, 478, 320, 20);
        tarjeta.add(estado);

        crear.addActionListener(e -> {
            char[] clave = contrasena.getPassword();
            char[] clave2 = confirmar.getPassword();
            if (!Arrays.equals(clave, clave2)) {
                estado.setText("Las contraseñas no coinciden.");
                Arrays.fill(clave, '\0');
                Arrays.fill(clave2, '\0');
                return;
            }
            try {
                Estudiante nuevo = controlador.registrarEstudiante(
                        nombre.getText(), correo.getText(), carrera.getText(), new String(clave));
                alRegistrar.accept(nuevo);
            } catch (RuntimeException ex) {
                estado.setText(ex.getMessage());
            } finally {
                Arrays.fill(clave, '\0');
                Arrays.fill(clave2, '\0');
                contrasena.setText("");
                confirmar.setText("");
            }
        });
        regresar.addActionListener(e -> volver.run());

        JPanel marco = new JPanel(new BorderLayout());
        marco.setBorder(EstilosAcceso.crearMarco());
        marco.add(tarjeta);
        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setOpaque(false);
        centrado.add(marco);
        JScrollPane desplazamiento = new JScrollPane(centrado);
        desplazamiento.setBorder(null);
        desplazamiento.setOpaque(false);
        desplazamiento.getViewport().setOpaque(false);
        desplazamiento.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        add(desplazamiento, BorderLayout.CENTER);
    }
}

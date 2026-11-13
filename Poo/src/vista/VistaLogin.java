package vista;

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
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class VistaLogin extends FondoDeportesArte {
    private static final long serialVersionUID = 1L;

    public VistaLogin(UsuarioController controlador, Consumer<Usuario> alIngresar, Runnable irARegistro) {
        this(controlador, alIngresar, irARegistro, null);
    }

    public VistaLogin(UsuarioController controlador, Consumer<Usuario> alIngresar,
                      Runnable irARegistro, Runnable irARegistroLider) {
        setLayout(new GridBagLayout());

        JPanel tarjeta = new JPanel(null);
        tarjeta.setPreferredSize(new Dimension(448, irARegistroLider == null ? 372 : 422));
        tarjeta.setBackground(EstilosAcceso.TARJETA);

        JLabel titulo = new JLabel("MY CLUB", SwingConstants.CENTER);
        titulo.setFont(EstilosAcceso.LOGO);
        titulo.setForeground(Color.BLACK);
        titulo.setBounds(0, 20, 448, 45);
        tarjeta.add(titulo);

        JLabel lema = new JLabel("Encuentra tu club y participa.");
        lema.setFont(EstilosAcceso.SERIF);
        lema.setBounds(64, 80, 320, 20);
        tarjeta.add(lema);

        JLabel textoCorreo = new JLabel("Correo institucional");
        textoCorreo.setFont(EstilosAcceso.ETIQUETA);
        textoCorreo.setBounds(64, 111, 320, 20);
        tarjeta.add(textoCorreo);

        JTextField correo = new JTextField();
        correo.setName("correo");
        EstilosAcceso.estilizarCampo(correo);
        correo.setBounds(64, 143, 320, 18);
        tarjeta.add(correo);

        JLabel textoContrasena = new JLabel("Contraseña");
        textoContrasena.setFont(EstilosAcceso.ETIQUETA);
        textoContrasena.setBounds(64, 174, 320, 20);
        tarjeta.add(textoContrasena);

        JPasswordField contrasena = new JPasswordField();
        contrasena.setName("contrasena");
        EstilosAcceso.estilizarCampo(contrasena);
        contrasena.setBounds(64, 207, 320, 18);
        tarjeta.add(contrasena);

        JButton ingresar = new JButton("Iniciar sesión");
        ingresar.setName("ingresar");
        EstilosAcceso.estilizarBotonVerde(ingresar);
        ingresar.setBounds(64, 240, 320, 32);
        tarjeta.add(ingresar);

        JButton registrar = new JButton("Crear cuenta de estudiante");
        registrar.setName("registrarEstudiante");
        EstilosAcceso.estilizarBotonClaro(registrar);
        registrar.setBounds(64, 290, 320, 33);
        tarjeta.add(registrar);

        if (irARegistroLider != null) {
            JButton lider = new JButton("Solicitar registro de líder");
            lider.setName("registrarLider");
            EstilosAcceso.estilizarBotonClaro(lider);
            lider.setBounds(64, 335, 320, 33);
            lider.addActionListener(e -> irARegistroLider.run());
            tarjeta.add(lider);
        }

        JLabel estado = new JLabel(" ");
        estado.setName("estadoLogin");
        estado.setFont(EstilosAcceso.ETIQUETA);
        estado.setForeground(Color.RED);
        estado.setBounds(64, irARegistroLider == null ? 335 : 385, 320, 20);
        tarjeta.add(estado);

        ingresar.addActionListener(e -> {
            char[] clave = contrasena.getPassword();
            if (correo.getText().trim().isEmpty() || clave.length == 0) {
                Arrays.fill(clave, '\0');
                estado.setText("Completa el correo y la contraseña.");
                return;
            }
            Usuario usuario = controlador.iniciarSesion(correo.getText(), new String(clave));
            Arrays.fill(clave, '\0');
            contrasena.setText("");
            if (usuario == null) estado.setText("Correo o contraseña incorrectos.");
            else alIngresar.accept(usuario);
        });
        registrar.addActionListener(e -> irARegistro.run());
        contrasena.addActionListener(e -> ingresar.doClick());

        JPanel marco = new JPanel(new BorderLayout());
        marco.setBorder(EstilosAcceso.crearMarco());
        marco.add(tarjeta);
        add(marco);
    }
}

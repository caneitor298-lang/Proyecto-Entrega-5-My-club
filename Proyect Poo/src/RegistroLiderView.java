import java.awt.*;
import java.util.Arrays;
import javax.swing.*;

public class RegistroLiderView extends JPanel {
    private static final long serialVersionUID = 1L;

    public RegistroLiderView(SolicitudesController controlador, Runnable volver) {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO_VERDE);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        JLabel titulo = new JLabel("Solicitud de registro de líder", SwingConstants.CENTER);
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.VERDE);
        int fila = 0;
        agregar(formulario, titulo, fila++);
        agregar(formulario, new JLabel("<html>Solo pueden solicitarlo cuentas ya creadas como <b>estudiante</b>.<br>"
                + "Si aún no tienes cuenta, regístrate primero como estudiante. "
                + "El administrador debe aprobar la solicitud.</html>"), fila++);

        JTextField carnet = campo("carnetLider");
        JTextField edad = campo("edadLider");
        JTextField nombreClub = campo("nombreClubLider");
        JTextField correo = campo("correoLider");
        JTextField ubicacion = campo("ubicacionClubLider");
        JTextField horario = campo("horarioClubLider");
        JTextArea descripcion = new JTextArea(4, 30);
        descripcion.setName("descripcionClubLider");
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        JComboBox<String> categoria = new JComboBox<>(new String[] {
            "Deportes", "Tecnología", "Arte", "Académicos", "Entretenimiento"
        });
        categoria.setName("categoriaClubLider");
        JPasswordField contrasena = new JPasswordField();
        contrasena.setName("contrasenaLider");

        fila = agregarCampo(formulario, "Correo de tu cuenta de estudiante", correo, fila);
        fila = agregarCampo(formulario, "Contraseña de tu cuenta de estudiante", contrasena, fila);
        fila = agregarCampo(formulario, "Carné", carnet, fila);
        fila = agregarCampo(formulario, "Edad", edad, fila);
        fila = agregarCampo(formulario, "Nombre del club que administrará", nombreClub, fila);
        fila = agregarCampo(formulario, "Categoría del club", categoria, fila);
        fila = agregarCampo(formulario, "Descripción del club", new JScrollPane(descripcion), fila);
        fila = agregarCampo(formulario, "Ubicación", ubicacion, fila);
        fila = agregarCampo(formulario, "Horario", horario, fila);

        JButton enviar = new JButton("Enviar solicitud de líder");
        enviar.setName("enviarSolicitudLider");
        Estilos.estilizarBoton(enviar);
        agregar(formulario, enviar, fila++);
        JButton regresar = new JButton("Volver al inicio de sesión");
        agregar(formulario, regresar, fila++);
        JTextArea estado = new JTextArea(2, 30);
        estado.setName("estadoRegistroLider");
        estado.setEditable(false);
        estado.setLineWrap(true);
        estado.setWrapStyleWord(true);
        estado.setOpaque(false);
        estado.setForeground(Estilos.ROJO);
        agregar(formulario, estado, fila++);

        enviar.addActionListener(e -> {
            char[] clave = contrasena.getPassword();
            try {
                int edadIngresada;
                try {
                    edadIngresada = Integer.parseInt(edad.getText().trim());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("La edad debe ser un número entero.");
                }
                SolicitudLider enviada = controlador.enviarSolicitud(
                    correo.getText(), new String(clave), carnet.getText(), edadIngresada,
                    nombreClub.getText(), descripcion.getText(),
                    (String) categoria.getSelectedItem(), ubicacion.getText(), horario.getText()
                );
                JOptionPane.showMessageDialog(this,
                    "Solicitud de " + enviada.getNombreClub() + " enviada y guardada. "
                    + "Cuando el administrador la acepte, tu cuenta pasará a rango Líder.");
                volver.run();
            } catch (RuntimeException ex) {
                estado.setText(ex.getMessage());
            } finally {
                Arrays.fill(clave, '\0');
                contrasena.setText("");
            }
        });
        regresar.addActionListener(e -> volver.run());

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.add(formulario, BorderLayout.NORTH);
        JScrollPane desplazamiento = new JScrollPane(contenedor);
        desplazamiento.setBorder(null);
        desplazamiento.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(18);
        add(desplazamiento, BorderLayout.CENTER);
    }

    private JTextField campo(String nombre) {
        JTextField campo = new JTextField(30);
        campo.setName(nombre);
        return campo;
    }

    private int agregarCampo(JPanel panel, String etiqueta, JComponent campo, int fila) {
        agregar(panel, new JLabel(etiqueta), fila++);
        campo.setFont(Estilos.TEXTO);
        agregar(panel, campo, fila++);
        return fila;
    }

    private void agregar(JPanel panel, JComponent componente, int fila) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = fila;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 0, 5, 0);
        panel.add(componente, c);
    }
}

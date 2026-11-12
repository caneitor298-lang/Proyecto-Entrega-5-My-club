import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MainAnuncios {
    public static void main(String[] args) {
        DatosAplicacion datos = DatosAplicacion.cargar();
        UsuarioController usuarios = new UsuarioController(datos);

        JTextField correo = new JTextField();
        JPasswordField contrasena = new JPasswordField();
        Object[] campos = {"Correo:", correo, "Contraseña:", contrasena};
        int opcion = JOptionPane.showConfirmDialog(null, campos, "Entrar como líder",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        Usuario usuario = usuarios.iniciarSesion(correo.getText(), new String(contrasena.getPassword()));
        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos.");
            System.exit(0);
        }
        if (!usuario.getRango().puedeAdministrarClub() || !(usuario instanceof Lider)) {
            JOptionPane.showMessageDialog(null, "Solo un líder puede editar o eliminar anuncios.");
            System.exit(0);
        }

        javax.swing.JFrame ventana = new javax.swing.JFrame("Anuncios de mi club");
        ventana.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(new EditarAnunciosView((Lider) usuario, datos, ventana::dispose));
        ventana.setSize(900, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}

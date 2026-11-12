import javax.swing.SwingUtilities;

public class MainClubExtras {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatosAplicacion datos = DatosAplicacion.cargar();
            UsuarioController usuarios = new UsuarioController(datos);
            ClubController clubes = new ClubController(datos);
            VentanaPrincipalExtra ventana = new VentanaPrincipalExtra(usuarios, clubes, datos);
            ventana.setVisible(true);
        });
    }
}
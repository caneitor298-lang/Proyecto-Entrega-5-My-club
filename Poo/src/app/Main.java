package app;

import controlador.ClubController;
import persistencia.DatosAplicacion;
import controlador.UsuarioController;
import vista.VentanaPrincipal;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatosAplicacion datos = DatosAplicacion.cargar();
        UsuarioController usuarios = new UsuarioController(datos);
        ClubController clubes = new ClubController(datos);
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(usuarios, clubes, datos);
            ventana.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        datos.guardar();
                    } catch (RuntimeException ex) {
                        VentanaPrincipal.mostrarError(ventana, ex);
                        ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                        return;
                    }
                    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            });
            ventana.setVisible(true);
        });
    }
}

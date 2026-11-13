package vista;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class EstilosAcceso {

    public static final Color FONDO_VENTANA = new Color(51, 63, 79);
    public static final Color TARJETA = new Color(204, 255, 255);
    public static final Color VERDE = new Color(0, 204, 153);
    public static final Color VERDE_CLARO = new Color(102, 255, 204);
    public static final Color BORDE_CLARO = new Color(0, 255, 204);
    public static final Color DEGRADADO_FINAL = new Color(0, 140, 120);
    public static final Color AZUL_MARCO = new Color(20, 35, 70);

    public static final Font LOGO = new Font("Segoe Script", Font.BOLD, 26);
    public static final Font ETIQUETA = new Font("Comic Sans MS", Font.PLAIN, 13);
    public static final Font SERIF = new Font("Comic Sans MS", Font.BOLD, 13);

    public static void estilizarBotonVerde(JButton boton) {
        boton.setBackground(VERDE);
        boton.setForeground(Color.BLACK);
        boton.setFont(SERIF);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
    }

    public static void estilizarBotonClaro(JButton boton) {
        boton.setBackground(VERDE_CLARO);
        boton.setForeground(Color.BLACK);
        boton.setFont(SERIF);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorder(BorderFactory.createLineBorder(BORDE_CLARO, 2));
        boton.setFocusPainted(false);
    }

    public static void estilizarCampo(JTextField campo) {
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
    }

    public static Border crearMarco() {
        Border exterior = BorderFactory.createLineBorder(AZUL_MARCO, 6);
        Border medio = BorderFactory.createLineBorder(VERDE, 4);
        Border interior = BorderFactory.createLineBorder(Color.WHITE, 3);
        return BorderFactory.createCompoundBorder(exterior,
                BorderFactory.createCompoundBorder(medio, interior));
    }
}

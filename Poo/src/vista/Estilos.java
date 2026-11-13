package vista;

import java.awt.*;
import javax.swing.*;

public class Estilos {

    
    public static final Color VERDE_OSCURO = new Color(0x0b, 0x9e, 0x51);   

    public static final Color VERDE = new Color(0x6a, 0xbf, 0x4b);         
    public static final Color VERDE_HOVER = new Color(9, 140, 72);          
    public static final Color VERDE_CLARO = new Color(230, 245, 232);        
    public static final Color VERDE_MUY_CLARO = new Color(240, 250, 242);    

    
    public static final Color AZUL_OSCURO = VERDE_OSCURO;
    public static final Color AZUL = VERDE_OSCURO;
    public static final Color AZUL_HOVER = VERDE_HOVER;
    public static final Color AZUL_CLARO = VERDE_CLARO;

    public static final Color FONDO = new Color(244, 248, 245);             
    public static final Color BLANCO = Color.WHITE;
    public static final Color GRIS_TEXTO = new Color(70, 90, 75);
    public static final Color BORDE = new Color(190, 215, 195);
    public static final Color EXITO = VERDE_OSCURO;
    public static final Color PELIGRO = new Color(190, 50, 50);
    // Nombres utilizados por el registro de líder y la vista del administrador.
    public static final Color FONDO_VERDE = FONDO;
    public static final Color ROJO = PELIGRO;

    public static final Font TITULO = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font SUBTITULO = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font TEXTO = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font TEXTO_PEQUENO = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BOTON = new Font("Segoe UI", Font.BOLD, 13);

    public static void estilizarBoton(JButton boton) {
        boton.setBackground(VERDE_OSCURO);
        boton.setForeground(BLANCO);
        boton.setFocusPainted(false);
        boton.setFont(BOTON);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
    }

    public static void estilizarBotonSecundario(JButton boton) {
        boton.setBackground(VERDE_MUY_CLARO);
        boton.setForeground(VERDE_OSCURO);
        boton.setFocusPainted(false);
        boton.setFont(BOTON);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VERDE_OSCURO, 1),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)
        ));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
    }

    public static void estilizarBotonAcento(JButton boton) {
        boton.setBackground(VERDE);
        boton.setForeground(BLANCO);
        boton.setFocusPainted(false);
        boton.setFont(BOTON);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
    }

    public static void estilizarBotonPeligro(JButton boton) {
        boton.setBackground(PELIGRO);
        boton.setForeground(BLANCO);
        boton.setFocusPainted(false);
        boton.setFont(BOTON);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
    }

    public static void estilizarCampo(JComponent campo) {
        campo.setFont(TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        campo.setBackground(BLANCO);
    }

    public static void estilizarTarjeta(JPanel panel) {
        panel.setBackground(VERDE_MUY_CLARO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
    }

    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(TEXTO);
        tabla.setForeground(GRIS_TEXTO);
        tabla.setRowHeight(40);
        tabla.setSelectionBackground(VERDE_CLARO);
        tabla.setSelectionForeground(VERDE_OSCURO);
        tabla.setGridColor(BORDE);
        tabla.getTableHeader().setBackground(VERDE_OSCURO);
        tabla.getTableHeader().setForeground(BLANCO);
        tabla.getTableHeader().setFont(BOTON);
    }
}

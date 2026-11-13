package vista;

import controlador.ValidadorImagen;
import modelo.Club;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Selector y escalado del trabajo visual de Sofía, integrado a las vistas MVC. */
public final class Imagenes {
    private Imagenes() { }
    public static byte[] elegirImagen(Component padre) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Elegir imagen");
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes (png, jpg, gif)", "png", "jpg", "jpeg", "gif"));
        if (selector.showOpenDialog(padre) != JFileChooser.APPROVE_OPTION) return null;
        File archivo = selector.getSelectedFile();
        try {
            if (!archivo.isFile() || archivo.length() > ValidadorImagen.MAX_BYTES) {
                throw new IllegalArgumentException("Selecciona una imagen de hasta 3 MB.");
            }
            byte[] datos = Files.readAllBytes(archivo.toPath());
            ValidadorImagen.validar(datos);
            return datos;
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(padre, ex.getMessage(), "Revisa la imagen", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    public static ImageIcon ajustar(byte[] datos, int maxAncho, int maxAlto) {
        if (datos == null) return null;
        Image original = new ImageIcon(datos).getImage();
        int ancho = original.getWidth(null), alto = original.getHeight(null);
        if (ancho <= 0 || alto <= 0) return null;
        double factor = Math.min(1.0, Math.min((double) maxAncho / ancho, (double) maxAlto / alto));
        int nuevoAncho = Math.max(1, (int) (ancho * factor));
        int nuevoAlto = Math.max(1, (int) (alto * factor));
        BufferedImage resultado = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultado.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, nuevoAncho, nuevoAlto, null);
        g.dispose();
        return new ImageIcon(resultado);
    }
    public static ImageIcon logo(Club club, int tam) {
        byte[] datos = club.getLogo();
        BufferedImage lienzo = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = lienzo.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ImageIcon icono = ajustar(datos, tam - 4, tam - 4);
        if (icono != null) {
            g.drawImage(icono.getImage(), (tam - icono.getIconWidth()) / 2,
                    (tam - icono.getIconHeight()) / 2, null);
        } else {
            g.setColor(Estilos.VERDE_OSCURO);
            g.fillRoundRect(0, 0, tam, tam, tam / 3, tam / 3);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, Math.max(12, tam / 2)));
            String inicial = club.getNombre().trim().isEmpty() ? "C" : club.getNombre().trim().substring(0, 1).toUpperCase();
            FontMetrics fm = g.getFontMetrics();
            g.drawString(inicial, (tam - fm.stringWidth(inicial)) / 2,
                    (tam - fm.getHeight()) / 2 + fm.getAscent());
        }
        g.dispose();
        return new ImageIcon(lienzo);
    }
}

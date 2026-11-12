import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Imagenes {
    private static final long TAMANO_MAXIMO = 3L * 1024 * 1024;

    public static byte[] elegirImagen(Component padre) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Elegir imagen");
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes (png, jpg, gif)", "png", "jpg", "jpeg", "gif"));
        if (selector.showOpenDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File archivo = selector.getSelectedFile();
        if (archivo.length() > TAMANO_MAXIMO) {
            JOptionPane.showMessageDialog(padre, "La imagen pesa más de 3 MB. Elige una más pequeña.",
                    "Imagen muy grande", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            byte[] datos = Files.readAllBytes(archivo.toPath());
            if (new ImageIcon(datos).getIconWidth() <= 0) {
                JOptionPane.showMessageDialog(padre, "El archivo no es una imagen válida.",
                        "Imagen no válida", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return datos;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo leer el archivo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static ImageIcon ajustar(byte[] datos, int maxAncho, int maxAlto) {
        Image original = new ImageIcon(datos).getImage();
        int ancho = original.getWidth(null);
        int alto = original.getHeight(null);
        if (ancho <= 0 || alto <= 0) {
            return null;
        }
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

    public static ImageIcon logoDelClub(Club club, int tam) {
        BufferedImage lienzo = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = lienzo.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        byte[] datos = ExtrasClub.obtener().getLogo(club);
        if (datos != null) {
            Image original = new ImageIcon(datos).getImage();
            int ancho = original.getWidth(null);
            int alto = original.getHeight(null);
            if (ancho > 0 && alto > 0) {
                double factor = Math.min((double) tam / ancho, (double) tam / alto);
                int nuevoAncho = Math.max(1, (int) (ancho * factor));
                int nuevoAlto = Math.max(1, (int) (alto * factor));
                g.drawImage(original, (tam - nuevoAncho) / 2, (tam - nuevoAlto) / 2, nuevoAncho, nuevoAlto, null);
            }
        }

        g.dispose();
        return new ImageIcon(lienzo);
    }
}
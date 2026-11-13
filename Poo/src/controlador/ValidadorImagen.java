package controlador;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/** Validación común para logos, fotos e imágenes de anuncios. */
public final class ValidadorImagen {
    public static final int MAX_BYTES = 3 * 1024 * 1024;
    private ValidadorImagen() { }

    public static void validar(byte[] datos) {
        if (datos == null) return;
        if (datos.length == 0 || datos.length > MAX_BYTES) {
            throw new IllegalArgumentException("Cada imagen debe pesar como máximo 3 MB y no puede estar vacía.");
        }
        try (ImageInputStream entrada = ImageIO.createImageInputStream(new ByteArrayInputStream(datos))) {
            if (entrada == null) throw new IOException("Imagen no legible");
            Iterator<ImageReader> lectores = ImageIO.getImageReaders(entrada);
            if (!lectores.hasNext()) throw new IOException("Formato no reconocido");
            ImageReader lector = lectores.next();
            try {
                lector.setInput(entrada, true, true);
                String formato = lector.getFormatName();
                if (!(formato.equalsIgnoreCase("png") || formato.equalsIgnoreCase("jpeg")
                        || formato.equalsIgnoreCase("jpg") || formato.equalsIgnoreCase("gif"))) {
                    throw new IOException("Formato no admitido");
                }
                int ancho = lector.getWidth(0), alto = lector.getHeight(0);
                if (ancho <= 0 || alto <= 0 || (long) ancho * alto > 16_000_000L) {
                    throw new IllegalArgumentException("La imagen debe tener como máximo 16 millones de píxeles.");
                }
                if (lector.read(0) == null) throw new IOException("Contenido no válido");
            } finally { lector.dispose(); }
        } catch (IOException | IndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("El archivo no es una imagen PNG, JPG o GIF válida.", ex);
        }
    }
}

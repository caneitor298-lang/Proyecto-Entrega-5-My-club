package modelo;

import java.io.Serializable;

public class Anuncio implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private byte[] imagen;
    public byte[] getImagen() { return imagen == null ? null : imagen.clone(); }
    public void setImagen(byte[] imagen) { this.imagen = imagen == null ? null : imagen.clone(); }

    private String titulo;
    private String mensaje;
    private String fecha;

    public Anuncio(String titulo, String mensaje, String fecha) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Anuncio{" +
                "titulo='" + titulo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }

}

package modelo;

import java.io.Serializable;
import java.util.ArrayList;
public class Club implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Anuncio> anuncios;

    private byte[] logo;
    private String contactos = "";
    private ArrayList<byte[]> fotos = new ArrayList<>();

    public byte[] getLogo() { return logo == null ? null : logo.clone(); }
    public void setLogo(byte[] logo) { this.logo = logo == null ? null : logo.clone(); }
    public String getContactos() { return contactos == null ? "" : contactos; }
    public void setContactos(String contactos) { this.contactos = contactos == null ? "" : contactos; }
    public ArrayList<byte[]> getFotos() {
        ArrayList<byte[]> copia = new ArrayList<>();
        if (fotos != null) for (byte[] foto : fotos) copia.add(foto.clone());
        return copia;
    }
    public void setFotos(java.util.List<byte[]> nuevas) {
        fotos = new ArrayList<>();
        if (nuevas != null) for (byte[] foto : nuevas) fotos.add(foto.clone());
    }

    private String nombre;
    private String descripcion;
    private String categoria;
    private String ubicacion;
    private String horario;

    public Club(String nombre, String descripcion, String categoria, String ubicacion, String horario) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.horario = horario;
        this.anuncios = new ArrayList<>();
    }

    public ArrayList<Anuncio> getAnuncios() {
        return anuncios;
    }

    public void agregarAnuncio(Anuncio anuncio) {
        anuncios.add(anuncio);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "Club{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria='" + categoria + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", horario='" + horario + '\'' +
                '}';
    }
}
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ExtrasClub implements Serializable {
    private static final long serialVersionUID = 1L;
    private static ExtrasClub instancia;

    private HashMap<String, byte[]> logos = new HashMap<>();
    private HashMap<String, String> contactos = new HashMap<>();
    private HashMap<String, ArrayList<byte[]>> imagenes = new HashMap<>();

    private static File archivo() {
        return DatosAplicacion.getRutaArchivo().resolveSibling("extras_clubes.dat").toFile();
    }

    public static ExtrasClub obtener() {
        if (instancia == null) {
            File f = archivo();
            if (f.exists()) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
                    instancia = (ExtrasClub) in.readObject();
                } catch (Exception e) {
                    instancia = null;
                }
            }
            if (instancia == null) {
                instancia = new ExtrasClub();
            }
        }
        return instancia;
    }

    public void guardar() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo()))) {
            out.writeObject(this);
        } catch (Exception e) {
            System.err.println("No se pudieron guardar los extras: " + e.getMessage());
        }
    }

    public byte[] getLogo(Club club) {
        return logos.get(club.getNombre());
    }

    public void setLogo(Club club, byte[] logo) {
        if (logo == null) {
            logos.remove(club.getNombre());
        } else {
            logos.put(club.getNombre(), logo);
        }
    }

    public String getContactos(Club club) {
        String texto = contactos.get(club.getNombre());
        return texto == null ? "" : texto;
    }

    public void setContactos(Club club, String texto) {
        contactos.put(club.getNombre(), texto);
    }

    public byte[] getImagenAnuncio(Club club, int indice) {
        ArrayList<byte[]> lista = imagenes.get(club.getNombre());
        if (lista == null || indice < 0 || indice >= lista.size()) {
            return null;
        }
        return lista.get(indice);
    }

    public void setImagenAnuncio(Club club, int indice, byte[] imagen) {
        ArrayList<byte[]> lista = imagenes.get(club.getNombre());
        if (lista == null) {
            lista = new ArrayList<>();
            imagenes.put(club.getNombre(), lista);
        }
        while (lista.size() <= indice) {
            lista.add(null);
        }
        lista.set(indice, imagen);
    }

    public void eliminarImagenAnuncio(Club club, int indice) {
        ArrayList<byte[]> lista = imagenes.get(club.getNombre());
        if (lista != null && indice >= 0 && indice < lista.size()) {
            lista.remove(indice);
        }
    }
}
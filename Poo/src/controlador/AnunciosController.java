package controlador;

import modelo.Anuncio;
import modelo.Club;
import persistencia.DatosAplicacion;
import modelo.Lider;
import modelo.TipoNotificacion;
import modelo.Usuario;
public class AnunciosController {
    private DatosAplicacion datos;

    public AnunciosController(DatosAplicacion datos) {
        this.datos = datos;
    }

    public void editarAnuncio(Lider lider, Anuncio anuncio, String titulo, String mensaje) {
        editarAnuncio(lider, anuncio, titulo, mensaje, anuncio == null ? null : anuncio.getImagen());
    }

    public void editarAnuncio(Lider lider, Anuncio anuncio, String titulo, String mensaje, byte[] imagen) {
        validar(lider, anuncio);
        ValidadorImagen.validar(imagen);
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (titulo.trim().length() > 100) {
            throw new IllegalArgumentException("El título admite hasta 100 caracteres.");
        }
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje es obligatorio.");
        }
        if (mensaje.trim().length() > 2000) {
            throw new IllegalArgumentException("El mensaje admite hasta 2000 caracteres.");
        }
        String tituloAnterior = anuncio.getTitulo();
        String mensajeAnterior = anuncio.getMensaje();
        byte[] imagenAnterior = anuncio.getImagen();
        if (tituloAnterior.equals(titulo.trim()) && mensajeAnterior.equals(mensaje.trim())
                && java.util.Arrays.equals(imagenAnterior, imagen)) return;
        NotificacionesController avisos = new NotificacionesController(datos);
        int anteriores = datos.getNotificaciones().size();
        anuncio.setTitulo(titulo.trim());
        anuncio.setMensaje(mensaje.trim());
        anuncio.setImagen(imagen);
        avisos.notificarMiembros(lider.getClubAdministrado(), TipoNotificacion.ANUNCIO,
                "Anuncio actualizado de " + lider.getClubAdministrado().getNombre(),
                anuncio.getTitulo() + "\n\n" + anuncio.getMensaje());
        try { datos.guardar(); }
        catch (RuntimeException ex) {
            anuncio.setTitulo(tituloAnterior);
            anuncio.setMensaje(mensajeAnterior);
            anuncio.setImagen(imagenAnterior);
            avisos.deshacerDesde(anteriores);
            throw ex;
        }
    }

    public void eliminarAnuncio(Lider lider, Anuncio anuncio) {
        validar(lider, anuncio);
        java.util.ArrayList<Anuncio> anuncios = lider.getClubAdministrado().getAnuncios();
        int posicion = anuncios.indexOf(anuncio);
        anuncios.remove(posicion);
        try { datos.guardar(); }
        catch (RuntimeException ex) { anuncios.add(posicion, anuncio); throw ex; }
    }

    private void validar(Lider lider, Anuncio anuncio) {
        if (lider == null || !datos.getUsuarios().contains(lider)) {
            throw new IllegalArgumentException("Usuario no registrado.");
        }
        if (!lider.getRango().puedeAdministrarClub()) {
            throw new IllegalArgumentException("Solo un líder puede editar anuncios.");
        }
        if (!datos.getClubes().contains(lider.getClubAdministrado())) {
            throw new IllegalArgumentException("Club no registrado.");
        }
        if (anuncio == null || !lider.getClubAdministrado().getAnuncios().contains(anuncio)) {
            throw new IllegalArgumentException("Selecciona un anuncio de tu club.");
        }
    }
}

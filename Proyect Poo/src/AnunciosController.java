public class AnunciosController {
    private DatosAplicacion datos;

    public AnunciosController(DatosAplicacion datos) {
        this.datos = datos;
    }

    public void editarAnuncio(Lider lider, Anuncio anuncio, String titulo, String mensaje) {
        validar(lider, anuncio);
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
        anuncio.setTitulo(titulo.trim());
        anuncio.setMensaje(mensaje.trim());
        datos.guardar();
    }

    public void eliminarAnuncio(Lider lider, Anuncio anuncio) {
        validar(lider, anuncio);
        lider.getClubAdministrado().getAnuncios().remove(anuncio);
        datos.guardar();
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

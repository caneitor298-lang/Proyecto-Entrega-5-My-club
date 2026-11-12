import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class ClubController {
    private final DatosAplicacion datos;
    public ClubController(DatosAplicacion datos) {
        this.datos = datos;
    }
    /** Búsqueda y categoría funcionan juntas, sin distinguir mayúsculas ni tildes. */
    public ArrayList<Club> buscarClubes(String nombre, String categoria) {
        ArrayList<Club> resultado = new ArrayList<>();
        String texto = normalizar(nombre == null ? "" : nombre.trim());
        for (Club club : datos.getClubes()) {
            boolean coincideCategoria = categoria == null || "Todas".equals(categoria) || club.getCategoria().equals(categoria);
            if (normalizar(club.getNombre()).contains(texto) && coincideCategoria) resultado.add(club);
        }
        return resultado;
    }
    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
    private void validarUsuarioYClub(Usuario usuario, Club club) {
        if (!datos.getUsuarios().contains(usuario) || !datos.getClubes().contains(club))
            throw new IllegalArgumentException("Usuario o club no registrado.");
    }
    public boolean unirEstudianteAClub(Estudiante estudiante, Club club) {
        validarUsuarioYClub(estudiante, club);
        validarRangoEstudiante(estudiante);
        if (!estudiante.unirseClub(club)) return false;
        datos.guardar();
        return true;
    }
    public boolean eliminarEstudianteDeClub(Estudiante estudiante, Club club) {
        validarUsuarioYClub(estudiante, club);
        validarRangoEstudiante(estudiante);
        if (!estudiante.salirClub(club)) return false;
        datos.guardar();
        return true;
    }
    public void publicarAnuncio(Usuario usuario, Club club, String titulo, String mensaje) {
        validarLider(usuario, club);
        validarTexto(titulo, "El título", 100);
        validarTexto(mensaje, "El mensaje", 2000);
        Anuncio anuncio = new Anuncio(titulo.trim(), mensaje.trim(), LocalDate.now().toString());
        ((Lider) usuario).agregarAnuncio(anuncio);
        datos.guardar();
    }
    public void cambiarHorarioClub(Usuario usuario, Club club, String horario) {
        validarLider(usuario, club);
        validarTexto(horario, "El horario", 150);
        ((Lider) usuario).cambiarhorario(horario.trim());
        datos.guardar();
    }

    public void actualizarInformacionClub(Usuario usuario, Club club, String descripcion, String ubicacion) {
        validarLider(usuario, club);
        validarTexto(descripcion, "La descripción", 2000);
        validarTexto(ubicacion, "La ubicación", 150);
        club.setDescripcion(descripcion.trim());
        club.setUbicacion(ubicacion.trim());
        datos.guardar();
    }

    public ArrayList<Estudiante> obtenerMiembros(Usuario usuarioLider, Club club) {
        validarLider(usuarioLider, club);
        ArrayList<Estudiante> miembros = new ArrayList<>();
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario instanceof Estudiante) {
                Estudiante estudiante = (Estudiante) usuario;
                if (estudiante.getClubes().contains(club)) miembros.add(estudiante);
            }
        }
        return miembros;
    }

    private void validarRangoEstudiante(Usuario usuario) {
        if (!usuario.getRango().puedeUnirseAClubes())
            throw new IllegalArgumentException("Solo un estudiante puede unirse a clubes o salir de ellos.");
    }

    private void validarLider(Usuario usuario, Club club) {
        validarUsuarioYClub(usuario, club);
        if (!usuario.getRango().puedeAdministrarClub() || !(usuario instanceof Lider)
                || ((Lider) usuario).getClubAdministrado() != club)
            throw new IllegalArgumentException("Solo el líder de este club puede modificarlo.");
    }
    private void validarTexto(String texto, String nombre, int maximo) {
        if (texto == null || texto.trim().isEmpty()) throw new IllegalArgumentException(nombre + " es obligatorio.");
        if (texto.trim().length() > maximo) throw new IllegalArgumentException(nombre + " admite hasta " + maximo + " caracteres.");
    }
}

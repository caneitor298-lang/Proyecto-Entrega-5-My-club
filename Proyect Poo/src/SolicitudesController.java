import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SolicitudesController {
    private final DatosAplicacion datos;

    public SolicitudesController(DatosAplicacion datos) {
        this.datos = datos;
    }

    public SolicitudLider enviarSolicitud(String correo, String contrasena, String carnet, int edad,
            String nombreClub, String descripcion, String categoria, String ubicacion, String horario) {
        correo = validarTexto(correo, "El correo", 150).toLowerCase(Locale.ROOT);
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        Usuario existente = buscarPorCorreo(correo);
        if (existente == null) {
            throw new IllegalArgumentException("Primero debes crear una cuenta de estudiante con ese correo.");
        }
        if (!existente.getRango().puedeSolicitarLiderazgo() || !(existente instanceof Estudiante)) {
            throw new IllegalArgumentException("Solo una cuenta de estudiante puede solicitar ser líder. "
                    + "Esa cuenta ya es " + existente.getRango() + ".");
        }
        if (!existente.getContrasena().equals(contrasena)) {
            throw new IllegalArgumentException("La contraseña no corresponde a la cuenta de estudiante.");
        }
        Estudiante estudiante = (Estudiante) existente;
        if (edad < 1 || edad > 120) {
            throw new IllegalArgumentException("Ingresa una edad válida, entre 1 y 120 años.");
        }
        carnet = validarTexto(carnet, "El carné", 20);
        if (!carnet.matches("[0-9]+")) {
            throw new IllegalArgumentException("El carné debe contener solo números.");
        }
        nombreClub = validarTexto(nombreClub, "El nombre del club", 100);
        descripcion = validarTexto(descripcion, "La descripción", 2000);
        categoria = validarTexto(categoria, "La categoría", 50);
        if (!Arrays.asList("Deportes", "Tecnología", "Arte", "Académicos", "Entretenimiento").contains(categoria)) {
            throw new IllegalArgumentException("Selecciona una categoría del catálogo de clubes.");
        }
        ubicacion = validarTexto(ubicacion, "La ubicación", 150);
        horario = validarTexto(horario, "El horario", 150);
        validarDisponibilidad(carnet, nombreClub);
        for (SolicitudLider solicitud : datos.getSolicitudesLider()) {
            if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE
                    && (solicitud.getCorreo().equalsIgnoreCase(correo)
                    || solicitud.getCarnet().equals(carnet)
                    || normalizar(solicitud.getNombreClub()).equals(normalizar(nombreClub)))) {
                throw new IllegalArgumentException("Ya existe una solicitud pendiente con ese correo, carné o club.");
            }
        }
        SolicitudLider solicitud = new SolicitudLider(carnet, estudiante.getNombre(), edad,
                estudiante.getCorreo(), estudiante.getCarrera(), null, nombreClub, descripcion,
                categoria, ubicacion, horario);
        datos.getSolicitudesLider().add(solicitud);
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            datos.getSolicitudesLider().remove(solicitud);
            throw ex;
        }
        return solicitud;
    }

    public ArrayList<SolicitudLider> obtenerSolicitudes(Usuario usuario, EstadoSolicitud estado) {
        validarAdministrador(usuario);
        ArrayList<SolicitudLider> resultado = new ArrayList<>();
        for (SolicitudLider solicitud : datos.getSolicitudesLider()) {
            if (estado == null || solicitud.getEstado() == estado) resultado.add(solicitud);
        }
        return resultado;
    }

    public Lider aceptarSolicitud(Usuario usuario, SolicitudLider solicitud) {
        Administrador administrador = validarAdministrador(usuario);
        validarPendiente(solicitud);
        // La cuenta debe seguir existiendo y seguir siendo de estudiante.
        int posicion = -1;
        for (int i = 0; i < datos.getUsuarios().size(); i++) {
            if (datos.getUsuarios().get(i).getCorreo().equalsIgnoreCase(solicitud.getCorreo())) posicion = i;
        }
        if (posicion < 0) {
            throw new IllegalArgumentException("La cuenta de estudiante del solicitante ya no existe.");
        }
        Usuario actual = datos.getUsuarios().get(posicion);
        if (!actual.getRango().puedeSolicitarLiderazgo() || !(actual instanceof Estudiante)) {
            throw new IllegalArgumentException("El solicitante ya no es estudiante: su cuenta es "
                    + actual.getRango() + ". Deniega la solicitud.");
        }
        // Otro registro pudo ocupar el carné o el nombre del club durante la espera.
        validarDisponibilidad(solicitud.getCarnet(), solicitud.getNombreClub());
        Club club = new Club(solicitud.getNombreClub(), solicitud.getDescripcion(),
                solicitud.getCategoria(), solicitud.getUbicacion(), solicitud.getHorario());
        Lider lider = new Lider(actual.getNombre(), actual.getCorreo(), actual.getContrasena(),
                club, solicitud.getCarnet(), solicitud.getEdad(), ((Estudiante) actual).getCarrera());
        datos.getClubes().add(club);
        datos.getUsuarios().set(posicion, lider);
        solicitud.resolver(EstadoSolicitud.ACEPTADA, administrador, "");
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            datos.getClubes().remove(club);
            datos.getUsuarios().set(posicion, actual);
            solicitud.restaurarPendiente(null);
            throw ex;
        }
        return lider;
    }

    public void denegarSolicitud(Usuario usuario, SolicitudLider solicitud, String motivo) {
        Administrador administrador = validarAdministrador(usuario);
        validarPendiente(solicitud);
        motivo = validarTexto(motivo, "El motivo de denegación", 500);
        solicitud.resolver(EstadoSolicitud.DENEGADA, administrador, motivo);
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            solicitud.restaurarPendiente(null);
            throw ex;
        }
    }

    private Administrador validarAdministrador(Usuario usuario) {
        if (usuario == null || !usuario.getRango().puedeRevisarSolicitudes()
                || !(usuario instanceof Administrador) || !datos.getUsuarios().contains(usuario)) {
            throw new IllegalArgumentException("Solo un administrador registrado puede revisar solicitudes.");
        }
        return (Administrador) usuario;
    }

    private void validarPendiente(SolicitudLider solicitud) {
        if (solicitud == null || !datos.getSolicitudesLider().contains(solicitud)) {
            throw new IllegalArgumentException("Selecciona una solicitud registrada.");
        }
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalArgumentException("Esta solicitud ya fue revisada.");
        }
    }

    private void validarDisponibilidad(String carnet, String nombreClub) {
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario instanceof Lider && carnet.equals(((Lider) usuario).getCarnet())) {
                throw new IllegalArgumentException("Ya existe un líder con ese carné.");
            }
        }
        for (Club club : datos.getClubes()) {
            if (normalizar(club.getNombre()).equals(normalizar(nombreClub))) {
                throw new IllegalArgumentException("Ya existe un club con ese nombre.");
            }
        }
    }

    private Usuario buscarPorCorreo(String correo) {
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) return usuario;
        }
        return null;
    }

    private String validarTexto(String texto, String nombre, int maximo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(nombre + " es obligatorio.");
        }
        if (texto.trim().length() > maximo) {
            throw new IllegalArgumentException(nombre + " admite hasta " + maximo + " caracteres.");
        }
        return texto.trim();
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}

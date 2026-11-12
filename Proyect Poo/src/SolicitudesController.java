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
        // La contraseña no se guarda en la solicitud: la cuenta ya existe.
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

    
    public SolicitudLider enviarSolicitudRenuncia(Usuario usuario) {
        if (usuario == null || !usuario.getRango().puedeSolicitarRenuncia() || !(usuario instanceof Lider)) {
            throw new IllegalArgumentException("Solo un líder activo puede solicitar volver a ser estudiante.");
        }
        if (!datos.getUsuarios().contains(usuario)) {
            throw new IllegalArgumentException("Tu cuenta no está registrada.");
        }
        Lider lider = (Lider) usuario;
        for (SolicitudLider s : datos.getSolicitudesLider()) {
            if (s.getEstado() == EstadoSolicitud.PENDIENTE
                    && s.getCorreo().equalsIgnoreCase(lider.getCorreo())) {
                throw new IllegalArgumentException("Ya tienes una solicitud pendiente. Espera la respuesta del administrador.");
            }
        }
        Club club = lider.getClubAdministrado();
        String nombreClub = club != null ? club.getNombre() : "(sin club)";
        String descripcion = club != null ? club.getDescripcion() : "";
        String categoria = club != null ? club.getCategoria() : "";
        String ubicacion = club != null ? club.getUbicacion() : "";
        String horario = club != null ? club.getHorario() : "";
        SolicitudLider solicitud = new SolicitudLider(
                lider.getCarnet(), lider.getNombre(), lider.getEdad(),
                lider.getCorreo(), lider.getCarrera(), null,
                nombreClub, descripcion, categoria, ubicacion, horario, true);
        datos.getSolicitudesLider().add(solicitud);
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            datos.getSolicitudesLider().remove(solicitud);
            throw ex;
        }
        return solicitud;
    }

    public Usuario aceptarSolicitud(Usuario usuario, SolicitudLider solicitud) {
        Administrador administrador = validarAdministrador(usuario);
        validarPendiente(solicitud);

        if (solicitud.isEsInvitacion()) {
            throw new IllegalArgumentException(
                    "Esta es una invitación de liderazgo. Solo el estudiante invitado puede aceptarla o rechazarla.");
        }

        int posicion = -1;
        for (int i = 0; i < datos.getUsuarios().size(); i++) {
            if (datos.getUsuarios().get(i).getCorreo().equalsIgnoreCase(solicitud.getCorreo())) posicion = i;
        }
        if (posicion < 0) {
            throw new IllegalArgumentException("La cuenta del solicitante ya no existe.");
        }
        Usuario actual = datos.getUsuarios().get(posicion);

        if (solicitud.isEsRenuncia()) {
            // Degradar líder → estudiante
            if (!(actual instanceof Lider) || !actual.getRango().puedeSolicitarRenuncia()) {
                throw new IllegalArgumentException("El solicitante ya no es líder: su cuenta es "
                        + actual.getRango() + ". Deniega la solicitud.");
            }
            Lider lider = (Lider) actual;
            Estudiante estudiante = new Estudiante(
                    lider.getNombre(), lider.getCorreo(),
                    lider.getCarrera().isEmpty() ? "Sin carrera" : lider.getCarrera(),
                    lider.getContrasena());
            datos.getUsuarios().set(posicion, estudiante);
            solicitud.resolver(EstadoSolicitud.ACEPTADA, administrador, "");
            try {
                datos.guardar();
            } catch (RuntimeException ex) {
                datos.getUsuarios().set(posicion, actual);
                solicitud.restaurarPendiente(null);
                throw ex;
            }
            return estudiante;
        }

        if (!actual.getRango().puedeSolicitarLiderazgo() || !(actual instanceof Estudiante)) {
            throw new IllegalArgumentException("El solicitante ya no es estudiante: su cuenta es "
                    + actual.getRango() + ". Deniega la solicitud.");
        }
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

    public ArrayList<Club> obtenerClubesSinLider(Usuario usuario) {
        validarAdministrador(usuario);
        ArrayList<Club> sinLider = new ArrayList<>();
        for (Club club : datos.getClubes()) {
            if (buscarLiderDeClub(club) == null) {
                sinLider.add(club);
            }
        }
        return sinLider;
    }

    public ArrayList<Estudiante> obtenerMiembrosClub(Usuario usuario, Club club) {
        validarAdministrador(usuario);
        if (club == null || !datos.getClubes().contains(club)) {
            throw new IllegalArgumentException("Selecciona un club registrado.");
        }
        ArrayList<Estudiante> miembros = new ArrayList<>();
        for (Usuario u : datos.getUsuarios()) {
            if (u instanceof Estudiante) {
                Estudiante e = (Estudiante) u;
                if (e.getClubes().contains(club)) {
                    miembros.add(e);
                }
            }
        }
        return miembros;
    }


    public SolicitudLider enviarInvitacionLiderazgo(Usuario usuarioAdmin, Club club, Estudiante miembro) {
        validarAdministrador(usuarioAdmin);
        if (club == null || !datos.getClubes().contains(club)) {
            throw new IllegalArgumentException("Selecciona un club registrado.");
        }
        if (miembro == null || !datos.getUsuarios().contains(miembro)) {
            throw new IllegalArgumentException("Selecciona un estudiante registrado.");
        }
        if (!(miembro instanceof Estudiante) || !miembro.getRango().puedeSolicitarLiderazgo()) {
            throw new IllegalArgumentException("Solo un estudiante puede recibir la invitación de liderazgo.");
        }
        if (!miembro.getClubes().contains(club)) {
            throw new IllegalArgumentException("Ese estudiante no es miembro del club \"" + club.getNombre() + "\".");
        }
        if (buscarLiderDeClub(club) != null) {
            throw new IllegalArgumentException("El club \"" + club.getNombre() + "\" ya tiene un líder activo.");
        }
        // No permitir otra solicitud pendiente del mismo correo o del mismo club
        for (SolicitudLider s : datos.getSolicitudesLider()) {
            if (s.getEstado() == EstadoSolicitud.PENDIENTE) {
                if (s.getCorreo().equalsIgnoreCase(miembro.getCorreo())) {
                    throw new IllegalArgumentException("Ese estudiante ya tiene una solicitud o invitación pendiente.");
                }
                if (s.isEsInvitacion() && normalizar(s.getNombreClub()).equals(normalizar(club.getNombre()))) {
                    throw new IllegalArgumentException("Ya hay una invitación pendiente para el club \"" + club.getNombre() + "\".");
                }
            }
        }

        String carnet = generarCarnetTemporal(miembro);
        SolicitudLider invitacion = new SolicitudLider(
                carnet,
                miembro.getNombre(),
                0,
                miembro.getCorreo(),
                miembro.getCarrera(),
                null,
                club.getNombre(),
                club.getDescripcion(),
                club.getCategoria(),
                club.getUbicacion(),
                club.getHorario(),
                false,   
                true);  

        datos.getSolicitudesLider().add(invitacion);
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            datos.getSolicitudesLider().remove(invitacion);
            throw ex;
        }
        return invitacion;
    }


    public ArrayList<SolicitudLider> obtenerInvitacionesPendientes(Usuario usuario) {
        if (usuario == null || !(usuario instanceof Estudiante)) {
            return new ArrayList<>();
        }
        ArrayList<SolicitudLider> lista = new ArrayList<>();
        for (SolicitudLider s : datos.getSolicitudesLider()) {
            if (s.getEstado() == EstadoSolicitud.PENDIENTE
                    && s.isEsInvitacion()
                    && s.getCorreo().equalsIgnoreCase(usuario.getCorreo())) {
                lista.add(s);
            }
        }
        return lista;
    }

   
    public Lider aceptarInvitacion(Usuario usuario, SolicitudLider invitacion) {
        if (usuario == null || !(usuario instanceof Estudiante)) {
            throw new IllegalArgumentException("Solo un estudiante puede aceptar una invitación de liderazgo.");
        }
        if (invitacion == null || !datos.getSolicitudesLider().contains(invitacion)) {
            throw new IllegalArgumentException("La invitación no existe.");
        }
        if (invitacion.getEstado() != EstadoSolicitud.PENDIENTE || !invitacion.isEsInvitacion()) {
            throw new IllegalArgumentException("Esta invitación ya no está pendiente.");
        }
        if (!invitacion.getCorreo().equalsIgnoreCase(usuario.getCorreo())) {
            throw new IllegalArgumentException("Esta invitación no es para tu cuenta.");
        }

        Estudiante estudiante = (Estudiante) usuario;
        Club club = buscarClubPorNombre(invitacion.getNombreClub());
        if (club == null) {
            throw new IllegalArgumentException("El club de la invitación ya no existe.");
        }
        if (buscarLiderDeClub(club) != null) {
            throw new IllegalArgumentException("El club ya tiene un líder. La invitación ya no es válida.");
        }
        if (!estudiante.getClubes().contains(club)) {
            throw new IllegalArgumentException("Ya no eres miembro de ese club. No puedes aceptar la invitación.");
        }

        int posicion = -1;
        for (int i = 0; i < datos.getUsuarios().size(); i++) {
            if (datos.getUsuarios().get(i).getCorreo().equalsIgnoreCase(estudiante.getCorreo())) {
                posicion = i;
                break;
            }
        }
        if (posicion < 0) {
            throw new IllegalArgumentException("Tu cuenta ya no está registrada.");
        }

        String carnet = invitacion.getCarnet();
        if (carnet == null || carnet.isEmpty()) {
            carnet = generarCarnetTemporal(estudiante);
        }
        for (Usuario u : datos.getUsuarios()) {
            if (u instanceof Lider && carnet.equals(((Lider) u).getCarnet())) {
                carnet = carnet + "1";
                break;
            }
        }

        Lider nuevoLider = new Lider(
                estudiante.getNombre(),
                estudiante.getCorreo(),
                estudiante.getContrasena(),
                club,
                carnet,
                0,
                estudiante.getCarrera());

        estudiante.getClubes().remove(club);
        datos.getUsuarios().set(posicion, nuevoLider);
        invitacion.resolverPorEstudiante(EstadoSolicitud.ACEPTADA, estudiante.getCorreo(), "");

        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            datos.getUsuarios().set(posicion, estudiante);
            estudiante.getClubes().add(club);
            invitacion.restaurarPendiente(null);
            throw ex;
        }
        return nuevoLider;
    }

    public void rechazarInvitacion(Usuario usuario, SolicitudLider invitacion, String motivo) {
        if (usuario == null || !(usuario instanceof Estudiante)) {
            throw new IllegalArgumentException("Solo un estudiante puede rechazar una invitación.");
        }
        if (invitacion == null || !datos.getSolicitudesLider().contains(invitacion)) {
            throw new IllegalArgumentException("La invitación no existe.");
        }
        if (invitacion.getEstado() != EstadoSolicitud.PENDIENTE || !invitacion.isEsInvitacion()) {
            throw new IllegalArgumentException("Esta invitación ya no está pendiente.");
        }
        if (!invitacion.getCorreo().equalsIgnoreCase(usuario.getCorreo())) {
            throw new IllegalArgumentException("Esta invitación no es para tu cuenta.");
        }
        if (motivo == null) motivo = "";
        if (motivo.trim().length() > 500) {
            throw new IllegalArgumentException("El motivo admite hasta 500 caracteres.");
        }

        invitacion.resolverPorEstudiante(EstadoSolicitud.DENEGADA, usuario.getCorreo(),
                motivo.trim().isEmpty() ? "Rechazada por el estudiante" : motivo.trim());
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            invitacion.restaurarPendiente(null);
            throw ex;
        }
    }

    private Club buscarClubPorNombre(String nombre) {
        if (nombre == null) return null;
        String n = normalizar(nombre);
        for (Club c : datos.getClubes()) {
            if (normalizar(c.getNombre()).equals(n)) return c;
        }
        return null;
    }

    private Lider buscarLiderDeClub(Club club) {
        for (Usuario u : datos.getUsuarios()) {
            if (u instanceof Lider) {
                Lider l = (Lider) u;
                if (l.getClubAdministrado() == club) {
                    return l;
                }
            }
        }
        return null;
    }

    private String generarCarnetTemporal(Estudiante estudiante) {
        String correo = estudiante.getCorreo() == null ? "" : estudiante.getCorreo();
        StringBuilder digitos = new StringBuilder();
        for (char c : correo.toCharArray()) {
            if (Character.isDigit(c)) digitos.append(c);
        }
        if (digitos.length() >= 5) {
            return digitos.substring(0, Math.min(8, digitos.length()));
        }
        int hash = Math.abs(correo.hashCode() % 100000000);
        return String.format("%08d", hash);
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

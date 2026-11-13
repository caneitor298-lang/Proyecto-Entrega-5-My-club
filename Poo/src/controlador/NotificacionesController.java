package controlador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import modelo.*;
import persistencia.DatosAplicacion;

/** Genera avisos y permite a cada cuenta consultar y marcar solo los suyos. */
public class NotificacionesController {
    private final DatosAplicacion datos;
    public NotificacionesController(DatosAplicacion datos) { this.datos = datos; }

    // Estos métodos internos participan en el mismo guardado de la operación de origen.
    void crear(String correo, TipoNotificacion tipo, String titulo, String mensaje) {
        datos.getNotificaciones().add(new Notificacion(correo, tipo, titulo, mensaje));
    }

    void notificarMiembros(Club club, TipoNotificacion tipo, String titulo, String mensaje) {
        Set<String> enviados = new HashSet<>();
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario instanceof Estudiante && ((Estudiante) usuario).getClubes().contains(club)
                    && enviados.add(usuario.getCorreo().toLowerCase(Locale.ROOT))) {
                crear(usuario.getCorreo(), tipo, titulo, mensaje);
            }
        }
    }

    void deshacerDesde(int cantidadAnterior) {
        datos.getNotificaciones().subList(cantidadAnterior, datos.getNotificaciones().size()).clear();
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null || !datos.getUsuarios().contains(usuario)
                || !(usuario instanceof Estudiante || usuario instanceof Lider)) {
            throw new IllegalArgumentException("Inicia sesión como estudiante o líder para ver tus notificaciones.");
        }
    }

    public ArrayList<Notificacion> obtenerNotificaciones(Usuario usuario, boolean soloPendientes) {
        validarUsuario(usuario);
        ArrayList<Notificacion> resultado = new ArrayList<>();
        for (Notificacion aviso : datos.getNotificaciones()) {
            if (aviso.getCorreoDestinatario().equalsIgnoreCase(usuario.getCorreo())
                    && (!soloPendientes || !aviso.isLeida())) resultado.add(aviso);
        }
        Collections.reverse(resultado);
        return resultado;
    }

    public int contarPendientes(Usuario usuario) {
        return obtenerNotificaciones(usuario, true).size();
    }

    public void marcarLeida(Usuario usuario, Notificacion aviso) {
        validarUsuario(usuario);
        if (aviso == null || !datos.getNotificaciones().contains(aviso)
                || !aviso.getCorreoDestinatario().equalsIgnoreCase(usuario.getCorreo())) {
            throw new IllegalArgumentException("Selecciona una notificación de tu cuenta.");
        }
        if (aviso.isLeida()) return;
        aviso.setLeida(true);
        try { datos.guardar(); }
        catch (RuntimeException ex) { aviso.setLeida(false); throw ex; }
    }

    public void marcarTodasLeidas(Usuario usuario) {
        ArrayList<Notificacion> pendientes = obtenerNotificaciones(usuario, true);
        if (pendientes.isEmpty()) return;
        for (Notificacion aviso : pendientes) aviso.setLeida(true);
        try { datos.guardar(); }
        catch (RuntimeException ex) {
            for (Notificacion aviso : pendientes) aviso.setLeida(false);
            throw ex;
        }
    }
}

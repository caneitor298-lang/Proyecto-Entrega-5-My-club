package modelo;

public enum TipoNotificacion {
    ANUNCIO("Anuncio"),
    CAMBIO_HORARIO("Cambio de horario"),
    SOLICITUD_ACEPTADA("Solicitud aceptada"),
    SOLICITUD_DENEGADA("Solicitud rechazada"),
    INVITACION_LIDERAZGO("Invitación de liderazgo"),
    RESPUESTA_INVITACION("Respuesta de liderazgo");

    private final String texto;
    TipoNotificacion(String texto) { this.texto = texto; }
    @Override public String toString() { return texto; }
}

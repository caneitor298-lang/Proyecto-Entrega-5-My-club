package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/** El correo conserva el destinatario aunque cambie su objeto o rango. */
public class Notificacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id = UUID.randomUUID().toString();
    private final String correoDestinatario;
    private final TipoNotificacion tipo;
    private final String titulo;
    private final String mensaje;
    private final LocalDateTime fecha = LocalDateTime.now();
    private boolean leida;

    public Notificacion(String correoDestinatario, TipoNotificacion tipo, String titulo, String mensaje) {
        this.correoDestinatario = correoDestinatario;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }
    public String getId() { return id; }
    public String getCorreoDestinatario() { return correoDestinatario; }
    public TipoNotificacion getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}

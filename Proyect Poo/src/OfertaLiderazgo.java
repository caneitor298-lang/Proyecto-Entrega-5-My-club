import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Oferta de transferencia de liderazgo: el líder actual propone a un miembro
 * del club que lo sustituya. El líder no puede dejar el cargo hasta que un
 * miembro acepte esta oferta.
 */
public class OfertaLiderazgo implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Estado {
        PENDIENTE("Pendiente"),
        ACEPTADA("Aceptada"),
        RECHAZADA("Rechazada"),
        CANCELADA("Cancelada");

        private final String texto;
        Estado(String texto) { this.texto = texto; }
        @Override public String toString() { return texto; }
    }

    private final String id = UUID.randomUUID().toString();
    private final LocalDateTime fecha = LocalDateTime.now();
    private final String correoLider;
    private final String nombreLider;
    private final String correoSucesor;
    private final String nombreSucesor;
    private final String nombreClub;
    private final String carnetLider;
    private final int edadLider;
    private final String carreraLider;
    private Estado estado = Estado.PENDIENTE;
    private LocalDateTime fechaRespuesta;

    public OfertaLiderazgo(String correoLider, String nombreLider, String correoSucesor,
                           String nombreSucesor, String nombreClub,
                           String carnetLider, int edadLider, String carreraLider) {
        this.correoLider = correoLider;
        this.nombreLider = nombreLider;
        this.correoSucesor = correoSucesor;
        this.nombreSucesor = nombreSucesor;
        this.nombreClub = nombreClub;
        this.carnetLider = carnetLider;
        this.edadLider = edadLider;
        this.carreraLider = carreraLider;
    }

    public String getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public String getCorreoLider() { return correoLider; }
    public String getNombreLider() { return nombreLider; }
    public String getCorreoSucesor() { return correoSucesor; }
    public String getNombreSucesor() { return nombreSucesor; }
    public String getNombreClub() { return nombreClub; }
    public String getCarnetLider() { return carnetLider; }
    public int getEdadLider() { return edadLider; }
    public String getCarreraLider() { return carreraLider; }
    public Estado getEstado() { return estado; }
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }

    void marcar(Estado nuevo) {
        this.estado = nuevo;
        this.fechaRespuesta = LocalDateTime.now();
    }

    void restaurarPendiente() {
        this.estado = Estado.PENDIENTE;
        this.fechaRespuesta = null;
    }
}

package modelo;

public enum EstadoSolicitud {
    PENDIENTE("Pendiente"), ACEPTADA("Aceptada"), DENEGADA("Denegada");

    private final String texto;

    EstadoSolicitud(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}

public enum Rango {
    ESTUDIANTE("Estudiante", 1),
    LIDER("Líder", 2),
    ADMINISTRADOR("Administrador", 3);

    private final String texto;
    private final int nivel;

    Rango(String texto, int nivel) {
        this.texto = texto;
        this.nivel = nivel;
    }

    public int getNivel() { return nivel; }

    public boolean esAlMenos(Rango minimo) { return nivel >= minimo.nivel; }

    public boolean puedeUnirseAClubes() { return this == ESTUDIANTE; }

    public boolean puedeAdministrarClub() { return this == LIDER; }

    public boolean puedeRevisarSolicitudes() { return this == ADMINISTRADOR; }

    public boolean puedeSolicitarLiderazgo() { return this == ESTUDIANTE; }

    @Override
    public String toString() { return texto; }
}

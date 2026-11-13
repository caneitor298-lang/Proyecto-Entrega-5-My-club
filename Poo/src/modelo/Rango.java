package modelo;

/**
 * Rangos (roles) del programa. Cada rango tiene un nivel y define qué puede hacer.
 * ESTUDIANTE &lt; LIDER &lt; ADMINISTRADOR. Toda verificación de permisos pasa por aquí.
 */
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

    /** Verdadero si este rango es igual o superior al indicado. */
    public boolean esAlMenos(Rango minimo) { return nivel >= minimo.nivel; }

    /** Unirse o salir de clubes: solo estudiantes. */
    public boolean puedeUnirseAClubes() { return this == ESTUDIANTE; }

    /** Publicar anuncios, cambiar horario/información y ver miembros de SU club. */
    public boolean puedeAdministrarClub() { return this == LIDER; }

    /** Revisar, aceptar o denegar solicitudes de líder. */
    public boolean puedeRevisarSolicitudes() { return this == ADMINISTRADOR; }

    /** Solicitar el rango de líder: solo quien hoy es estudiante. */
    public boolean puedeSolicitarLiderazgo() { return this == ESTUDIANTE; }

    /** Solicitar renunciar al liderazgo y volver a estudiante: solo un líder. */
    public boolean puedeSolicitarRenuncia() { return this == LIDER; }

    @Override
    public String toString() { return texto; }
}

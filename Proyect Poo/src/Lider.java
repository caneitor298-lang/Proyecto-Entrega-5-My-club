import java.io.Serializable;

public class Lider extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private Club clubAdministrado;
    private String carnet;
    private int edad;
    private String carrera;

    public Lider(String nombre, String correo, String contrasena, Club clubAdministrado) {
        this(nombre, correo, contrasena, clubAdministrado, "", 0);
    }

    public Lider(String nombre, String correo, String contrasena, Club clubAdministrado, String carnet, int edad) {
        this(nombre, correo, contrasena, clubAdministrado, carnet, edad, "");
    }

    public Lider(String nombre, String correo, String contrasena, Club clubAdministrado,
                 String carnet, String carrera) {
        this(nombre, correo, contrasena, clubAdministrado, carnet, 0, carrera);
    }

    public Lider(String nombre, String correo, String contrasena, Club clubAdministrado,
                 String carnet, int edad, String carrera) {
        super(nombre, correo, contrasena);
        this.clubAdministrado = clubAdministrado;
        this.carnet = carnet;
        this.edad = edad;
        this.carrera = carrera;
    }

    @Override
    public Rango getRango() {
        return Rango.LIDER;
    }

    public Club getClubAdministrado() {
        return clubAdministrado;
    }

    public void setClubAdministrado(Club clubAdministrado) {
        this.clubAdministrado = clubAdministrado;
    }

    public String getCarnet() {
        return carnet == null ? "" : carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getCarrera() {
        return carrera == null ? "" : carrera;
    }

    public void agregarAnuncio(Anuncio anuncio) {
        clubAdministrado.agregarAnuncio(anuncio);
    }

    public void cambiarhorario(String nuevoHorario) {
        clubAdministrado.setHorario(nuevoHorario);
    }
}

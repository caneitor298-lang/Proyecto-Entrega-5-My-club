import java.io.Serializable;
import java.util.ArrayList;
public class Estudiante extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String carrera;
    private ArrayList<Club> clubes;

    public Estudiante(String nombre, String correo, String carrera, String contrasena) {
        super(nombre, correo, contrasena);
        this.carrera = carrera;
        this.clubes = new ArrayList<>();
    }

    @Override
    public Rango getRango() {
        return Rango.ESTUDIANTE;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public ArrayList<Club> getClubes() {
        return clubes;
    }

    public void setClubes(ArrayList<Club> clubes) {
        this.clubes = clubes;
    }

    public boolean unirseClub(Club club) {
        if (!clubes.contains(club)) {
            clubes.add(club);
            return true;
        }
        return false;
    }

    public boolean salirClub(Club club) {
        if (clubes.contains(club)) {
            clubes.remove(club);
            return true;
        }
        return false;
    }
}


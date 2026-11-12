public class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;

    public Administrador(String nombre, String correo, String contrasena) {
        super(nombre, correo, contrasena);
    }

    @Override
    public Rango getRango() {
        return Rango.ADMINISTRADOR;
    }
}

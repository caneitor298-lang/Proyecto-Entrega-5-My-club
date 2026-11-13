package vista;

import modelo.Usuario;
import controlador.UsuarioController;
import java.util.function.Consumer;

/** Mantiene el nombre anterior y utiliza el diseño de acceso agregado por Sofía. */
public class LoginView extends VistaLogin {
    private static final long serialVersionUID = 1L;

    public LoginView(UsuarioController controlador, Consumer<Usuario> alIngresar, Runnable irARegistro) {
        super(controlador, alIngresar, irARegistro);
    }

    public LoginView(UsuarioController controlador, Consumer<Usuario> alIngresar,
                     Runnable irARegistro, Runnable irARegistroLider) {
        super(controlador, alIngresar, irARegistro, irARegistroLider);
    }
}

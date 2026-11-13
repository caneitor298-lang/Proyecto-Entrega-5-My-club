package vista;

import modelo.Usuario;
import controlador.UsuarioController;
import java.util.function.Consumer;

/** Mantiene el nombre anterior y utiliza el formulario de estudiante agregado por Sofía. */
public class RegistroView extends VistaRegistro {
    private static final long serialVersionUID = 1L;

    public RegistroView(UsuarioController controlador, Consumer<Usuario> alRegistrar, Runnable volver) {
        super(controlador, alRegistrar, volver);
    }
}

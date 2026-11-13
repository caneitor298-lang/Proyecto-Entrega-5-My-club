package controlador;

import persistencia.DatosAplicacion;
import modelo.Estudiante;
import modelo.Usuario;
public class UsuarioController {
    private final DatosAplicacion datos;
    public UsuarioController(DatosAplicacion datos) { this.datos = datos; }
    public Usuario iniciarSesion(String correo, String contrasena) {
        if (correo == null || correo.trim().isEmpty() || contrasena == null || contrasena.isEmpty()) return null;
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getCorreo().equalsIgnoreCase(correo.trim())
                    && usuario.getContrasena().equals(contrasena)) return usuario;
        }
        return null;
    }


    public void actualizarPerfil(Estudiante estudiante, String nombre, String carrera, String actual, String nueva, String confirmar) {
    if (estudiante == null || !datos.getUsuarios().contains(estudiante)) {
        throw new IllegalArgumentException("Estudiante no registrado.");
    }
    if (actual == null || nueva == null || confirmar == null) {
        throw new IllegalArgumentException("Revisa los campos de contraseña.");
    }

    if (nombre == null || nombre.trim().isEmpty()
            || carrera == null || carrera.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "El nombre y la carrera son obligatorios."
        );
    }

    if (!nueva.isEmpty() || !confirmar.isEmpty()
            || !actual.isEmpty()) {

        if (!estudiante.getContrasena().equals(actual)) {
            throw new IllegalArgumentException(
                "La contraseña actual es incorrecta."
            );
        }

        if (nueva.length() < 8) {
            throw new IllegalArgumentException(
                "La nueva contraseña debe tener al menos 8 caracteres."
            );
        }

        if (!nueva.equals(confirmar)) {
            throw new IllegalArgumentException(
                "Las contraseñas nuevas no coinciden."
            );
        }

        estudiante.setContrasena(nueva);
    }

        estudiante.setNombre(nombre.trim());
        estudiante.setCarrera(carrera.trim());
        datos.guardar();
    }

    /** Crea una cuenta de estudiante nueva. Lanza una excepción si algún dato no es válido. */
    public Estudiante registrarEstudiante(String nombre, String correo, String carrera, String contrasena) {
        // 1. Ningún campo vacío
        if (nombre == null || nombre.trim().isEmpty()
                || correo == null || correo.trim().isEmpty()
                || carrera == null || carrera.trim().isEmpty()
                || contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("Completa todos los campos.");
        }

        nombre = nombre.trim();
        correo = correo.trim();
        carrera = carrera.trim();

        // 2. Correo institucional
        if (!correo.toLowerCase().endsWith("@uvg.edu.gt")) {
            throw new IllegalArgumentException("Usa tu correo institucional (@uvg.edu.gt).");
        }

        // 3. Correo no repetido
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                throw new IllegalArgumentException("Ya existe una cuenta con ese correo.");
            }
        }

        // 4. Contraseña de al menos 8 caracteres
        if (contrasena.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        // 5. Crear y guardar el estudiante
        Estudiante nuevo = new Estudiante(nombre, correo, carrera, contrasena);
        datos.getUsuarios().add(nuevo);
        datos.guardar();
        return nuevo;
    }
}

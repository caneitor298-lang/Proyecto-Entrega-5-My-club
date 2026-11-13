package persistencia;

import modelo.Administrador;
import modelo.Club;
import modelo.Estudiante;
import modelo.Lider;
import app.Main;
import modelo.SolicitudLider;
import modelo.Usuario;
import modelo.Notificacion;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

/** Objetos compartidos por las vistas y controladores durante la ejecución. */
public class DatosAplicacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO = "datos_aplicacion.dat";

    private final ArrayList<Club> clubes = new ArrayList<>();
    private final ArrayList<Usuario> usuarios = new ArrayList<>();
    private ArrayList<SolicitudLider> solicitudesLider = new ArrayList<>();
    private ArrayList<Notificacion> notificaciones = new ArrayList<>();
    public ArrayList<Notificacion> getNotificaciones() {
        if (notificaciones == null) notificaciones = new ArrayList<>();
        return notificaciones;
    }
    public ArrayList<Club> getClubes() { return clubes; }
    public ArrayList<Usuario> getUsuarios() { return usuarios; }

    /** Usa el mismo archivo aunque Main se ejecute desde otra carpeta del editor. */
    public static Path getRutaArchivo() {
        String personalizada = System.getProperty("myclub.datos");
        if (personalizada != null && !personalizada.trim().isEmpty()) {
            return Paths.get(personalizada).toAbsolutePath().normalize();
        }
        try {
            Path clases = Paths.get(DatosAplicacion.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            if (Files.isDirectory(clases) && clases.getFileName() != null
                    && "bin".equals(clases.getFileName().toString())
                    && Files.isDirectory(clases.getParent().resolve("src"))) {
                return clases.getParent().resolve(ARCHIVO).toAbsolutePath().normalize();
            }
        } catch (Exception ex) {
            // En otros empaquetados se conserva la carpeta de ejecución habitual.
        }
        Path actual = Paths.get("").toAbsolutePath();
        if (Files.isDirectory(actual.resolve("Proyect Poo").resolve("src"))) {
            return actual.resolve("Proyect Poo").resolve(ARCHIVO).normalize();
        }
        return Paths.get(ARCHIVO).toAbsolutePath().normalize();
    }
    public ArrayList<SolicitudLider> getSolicitudesLider() {
        // Los archivos de entregas anteriores todavía no tenían este atributo.
        if (solicitudesLider == null) solicitudesLider = new ArrayList<>();
        return solicitudesLider;
    }

    public void guardar() {
        Path destino = getRutaArchivo();
        Path temporal = null;
        try {
            temporal = Files.createTempFile(destino.getParent(), "myclub-", ".tmp");
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temporal))) {
                out.writeObject(this);
            }
            try {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron guardar los datos. "
                    + "Revisa los permisos de la carpeta e intenta de nuevo.", e);
        } finally {
            if (temporal != null) {
                try { Files.deleteIfExists(temporal); }
                catch (IOException ex) { temporal.toFile().deleteOnExit(); }
            }
        }
    }

    public static DatosAplicacion cargar() {
        File archivo = getRutaArchivo().toFile();
        if (!archivo.exists()) {
            return crearDemostracion();
        }
        try (ObjectInputStream in = abrirEntrada(new FileInputStream(archivo))) {
            Object objeto = in.readObject();
            if (objeto instanceof DatosAplicacion) {
                DatosAplicacion datos = (DatosAplicacion) objeto;
                datos.getSolicitudesLider();
                datos.getNotificaciones();
                datos.asegurarAdministrador();
                return datos;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo cargar la persistencia; se usará la demo: " + e.getMessage());
        }
        return crearDemostracion();
    }

    /** Compatibilidad con los .dat anteriores a la separación en paquetes MVC. */
    public static ObjectInputStream abrirEntrada(InputStream entrada) throws IOException {
        return new ObjectInputStream(entrada) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass descriptor)
                    throws IOException, ClassNotFoundException {
                String nombre = descriptor.getName();
                if ("DatosAplicacion".equals(nombre)) return DatosAplicacion.class;
                switch (nombre) {
                    case "Usuario": case "Estudiante": case "Lider": case "Administrador":
                    case "Club": case "Anuncio": case "Rango": case "EstadoSolicitud":
                    case "SolicitudLider": case "OfertaLiderazgo": case "OfertaLiderazgo$Estado":
                        return Class.forName("modelo." + nombre);
                    default: return super.resolveClass(descriptor);
                }
            }
        };
    }

    private void asegurarAdministrador() {
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Administrador) return;
        }
        String correo = "admin@uvg.edu.gt";
        int numero = 1;
        while (correoOcupado(correo)) correo = "admin" + numero++ + "@uvg.edu.gt";
        usuarios.add(new Administrador("Administrador My Club", correo, "Admin1234"));
    }

    private boolean correoOcupado(String correo) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) return true;
        }
        return false;
    }

    public static DatosAplicacion crearDemostracion() {
        DatosAplicacion datos = new DatosAplicacion();
        datos.clubes.add(new Club("Videojuegos", "Partidas amistosas y torneos entre estudiantes.",
                "Entretenimiento", "Aula 101", "Viernes, 15:00 a 17:00"));
        datos.clubes.add(new Club("Robótica", "Construcción y programación de robots.",
                "Tecnología", "Laboratorio 2", "Martes, 14:00 a 16:00"));
        datos.clubes.add(new Club("Fútbol", "Entrenamientos para todos los niveles.",
                "Deportes", "Cancha", "Miércoles, 16:00 a 18:00"));
        datos.clubes.add(new Club("Fotografía", "Práctica de fotografía y composición visual.",
                "Arte", "Aula 202", "Jueves, 14:00 a 15:30"));
        datos.clubes.add(new Club("Matemática", "Resolución de problemas y grupos de estudio.",
                "Académicos", "Biblioteca", "Lunes, 13:00 a 14:00"));
        datos.clubes.add(new Club("Programación", "Proyectos de software en equipo.",
                "Tecnología", "Laboratorio 3", "Sábado, 09:00 a 11:00"));
        datos.usuarios.add(new Estudiante("Emilio Cano", "emilio.cano@uvg.edu.gt",
                "Ciencias de la Computación", "Estudiante123"));
        datos.usuarios.add(new Estudiante("Estudiante de prueba", "estudiante@uvg.edu.gt",
                "Ingeniería", "Estudiante123"));
        datos.usuarios.add(new Lider("Líder de Videojuegos", "lider.videojuegos@uvg.edu.gt",
                "Lider1234", datos.clubes.get(0)));
        datos.usuarios.add(new Lider("Líder de Robótica", "lider.robotica@uvg.edu.gt",
                "Lider1234", datos.clubes.get(1)));
        datos.asegurarAdministrador();
        return datos;
    }
}

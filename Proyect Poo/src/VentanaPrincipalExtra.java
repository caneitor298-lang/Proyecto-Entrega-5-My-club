import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class VentanaPrincipalExtra extends JFrame {
    private final UsuarioController usuarios;
    private final ClubController clubes;
    private final DatosAplicacion datos;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final JPanel menu = new JPanel(new BorderLayout(8, 8));
    private Usuario usuario;
    private InicioViewExtra inicio;
    private JPanel detalle;
    private JPanel misClubes;
    private JPanel edicion;
    private AdministradorView solicitudes;

    public VentanaPrincipalExtra(UsuarioController usuarios, ClubController clubes, DatosAplicacion datos) {
        this.usuarios = usuarios;
        this.clubes = clubes;
        this.datos = datos;
        setTitle("My Club");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(940, 680);
        setMinimumSize(new Dimension(740, 560));
        setLocationRelativeTo(null);
        contenido.setBackground(Estilos.FONDO);
        getContentPane().setBackground(Estilos.FONDO);
        add(menu, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
        mostrarLogin();
    }

    public void mostrarLogin() {
        usuario = null;
        inicio = null;
        detalle = null;
        misClubes = null;
        edicion = null;
        solicitudes = null;
        menu.removeAll();
        menu.setVisible(false);
        contenido.removeAll();
        contenido.add(new LoginView(usuarios, this::abrirSesion, this::mostrarRegistro,
                this::mostrarRegistroLider), "login");
        tarjetas.show(contenido, "login");
        contenido.revalidate();
        contenido.repaint();
    }

    public void mostrarRegistro() {
        contenido.add(new RegistroView(usuarios, this::abrirSesion, this::mostrarLogin), "registro");
        tarjetas.show(contenido, "registro");
        contenido.revalidate();
        contenido.repaint();
    }

    public void mostrarRegistroLider() {
        contenido.add(new RegistroLiderView(new SolicitudesController(datos), this::mostrarLogin), "registroLider");
        tarjetas.show(contenido, "registroLider");
        contenido.revalidate();
        contenido.repaint();
    }

    private void abrirSesion(Usuario usuario) {
        this.usuario = usuario;
        contenido.removeAll();
        menu.removeAll();
        menu.setBackground(Estilos.VERDE_MUY_CLARO);
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Estilos.VERDE_OSCURO),
                BorderFactory.createEmptyBorder(8, 16, 10, 16)));

        JLabel marca = new JLabel("MY CLUB  ·  " + usuario.getNombre() + "  ·  "
                + (usuario instanceof Administrador ? "Administrador"
                        : usuario instanceof Lider ? "Líder" : "Estudiante"));
        marca.setFont(Estilos.SUBTITULO);
        marca.setForeground(Estilos.VERDE_OSCURO);
        menu.add(marca, BorderLayout.NORTH);

        JPanel navegacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        navegacion.setOpaque(false);
        menu.add(navegacion, BorderLayout.CENTER);

        if (usuario instanceof Administrador) {
            solicitudes = new AdministradorView(usuario, new SolicitudesController(datos));
            contenido.add(solicitudes, "solicitudes");
            JButton revisar = new JButton("Solicitudes de líderes");
            Estilos.estilizarBoton(revisar);
            revisar.addActionListener(e -> {
                solicitudes.refrescar();
                tarjetas.show(contenido, "solicitudes");
            });
            navegacion.add(revisar);
            JButton cerrar = new JButton("Cerrar sesión");
            Estilos.estilizarBotonPeligro(cerrar);
            cerrar.addActionListener(e -> mostrarLogin());
            navegacion.add(cerrar);
            menu.setVisible(true);
            tarjetas.show(contenido, "solicitudes");
            contenido.revalidate();
            contenido.repaint();
            return;
        }

        JButton explorar = new JButton("Explorar clubes");
        Estilos.estilizarBoton(explorar);
        explorar.addActionListener(e -> mostrarInicio());
        navegacion.add(explorar);

        if (usuario instanceof Estudiante) {
            JButton mis = new JButton("Mis clubes");
            Estilos.estilizarBoton(mis);
            mis.addActionListener(e -> mostrarMisClubes());
            navegacion.add(mis);

            JButton perfil = new JButton("Mi perfil");
            Estilos.estilizarBoton(perfil);
            perfil.addActionListener(e -> mostrarPerfil());
            navegacion.add(perfil);
        } else {
            JButton administrar = new JButton("Administrar mi club");
            Estilos.estilizarBoton(administrar);
            administrar.addActionListener(e -> mostrarClub(((Lider) usuario).getClubAdministrado(), this::mostrarInicio));
            navegacion.add(administrar);
        }

        JButton salir = new JButton("Cerrar sesión");
        Estilos.estilizarBotonPeligro(salir);
        salir.addActionListener(e -> mostrarLogin());
        navegacion.add(salir);

        menu.setVisible(true);
        inicio = new InicioViewExtra(clubes, club -> mostrarClub(club, this::mostrarInicio));
        contenido.add(inicio, "inicio");
        mostrarInicio();
    }

    public void mostrarInicio() {
        inicio.refrescar();
        tarjetas.show(contenido, "inicio");
        contenido.revalidate();
        contenido.repaint();
    }

    public void mostrarMisClubes() {
        if (!(usuario instanceof Estudiante)) return;
        if (misClubes != null) contenido.remove(misClubes);
        misClubes = new MisClubsViewExtra((Estudiante) usuario, clubes,
                club -> mostrarClub(club, this::mostrarMisClubes), this::mostrarMisClubes);
        contenido.add(misClubes, "mis");
        tarjetas.show(contenido, "mis");
        contenido.revalidate();
        contenido.repaint();
    }

    public void mostrarClub(Club club, Runnable volver) {
        if (detalle != null) contenido.remove(detalle);
        detalle = new ClubViewExtra(usuario, club, clubes, volver, () -> mostrarClub(club, volver),
                () -> mostrarEdicionAnuncios(club, volver));
        contenido.add(detalle, "detalle");
        tarjetas.show(contenido, "detalle");
        contenido.revalidate();
        contenido.repaint();
    }

    public static void mostrarError(Component padre, Exception ex) {
        JOptionPane.showMessageDialog(padre, ex.getMessage(), "Revisa la operación", JOptionPane.ERROR_MESSAGE);
    }

    private boolean puedeAdministrar(Club club) {
        return usuario instanceof Lider && ((Lider) usuario).getClubAdministrado() == club;
    }

    private void mostrarEdicionAnuncios(Club club, Runnable volver) {
        if (!puedeAdministrar(club)) return;
        if (edicion != null) contenido.remove(edicion);
        edicion = new EditarAnunciosViewExtra((Lider) usuario, datos, () -> mostrarClub(club, volver));
        contenido.add(edicion, "edicion");
        tarjetas.show(contenido, "edicion");
        contenido.revalidate();
        contenido.repaint();
    }

    public void mostrarPerfil() {
        if (!(usuario instanceof Estudiante)) return;
        PerfilView perfil = new PerfilView((Estudiante) usuario, usuarios, datos, () -> abrirSesion(usuario));
        contenido.add(perfil, "perfil");
        tarjetas.show(contenido, "perfil");
        contenido.revalidate();
        contenido.repaint();
    }
}
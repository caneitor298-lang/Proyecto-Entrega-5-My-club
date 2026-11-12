import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SelectorImagen extends JPanel {
    private byte[] imagen;
    private final int ancho;
    private final int alto;
    private final JLabel vista = new JLabel("Sin imagen", SwingConstants.CENTER);

    public SelectorImagen(int ancho, int alto, String textoBoton) {
        this.ancho = ancho;
        this.alto = alto;
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        vista.setPreferredSize(new Dimension(ancho, alto));
        vista.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        add(vista);

        JButton elegir = new JButton(textoBoton);
        JButton quitar = new JButton("Quitar imagen");

        JPanel botones = new JPanel(new GridLayout(0, 1, 6, 6));
        botones.add(elegir);
        botones.add(quitar);
        add(botones);

        elegir.addActionListener(e -> {
            byte[] nueva = Imagenes.elegirImagen(this);
            if (nueva != null) {
                setImagen(nueva);
            }
        });
        quitar.addActionListener(e -> setImagen(null));
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] datos) {
        this.imagen = datos;
        ImageIcon icono = null;
        if (datos != null) {
            icono = Imagenes.ajustar(datos, ancho - 4, alto - 4);
        }
        vista.setIcon(icono);
        vista.setText(icono == null ? "Sin imagen" : "");
    }
}
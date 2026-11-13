package vista;

import java.awt.*;
import javax.swing.JPanel;

/** Fondo de apoyo: esta dependencia no estaba incluida en el commit de las vistas. */
public class FondoDeportesArte extends JPanel {
    private static final long serialVersionUID = 1L;

    public FondoDeportesArte() {
        setBackground(EstilosAcceso.FONDO_VENTANA);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D fondo = (Graphics2D) g.create();
        fondo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        fondo.setPaint(new GradientPaint(0, 0, EstilosAcceso.FONDO_VENTANA,
                getWidth(), getHeight(), EstilosAcceso.AZUL_MARCO));
        fondo.fillRect(0, 0, getWidth(), getHeight());
        fondo.setColor(new Color(0, 204, 153, 25));
        fondo.fillOval(-100, -100, 330, 330);
        fondo.fillOval(getWidth() - 190, getHeight() - 190, 320, 320);
        fondo.dispose();
    }
}

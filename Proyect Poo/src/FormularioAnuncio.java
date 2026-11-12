import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FormularioAnuncio extends JPanel {
    private final JTextField titulo = new JTextField();
    private final JTextArea mensaje = new JTextArea(7, 34);
    private final SelectorImagen selectorImagen = new SelectorImagen(120, 90, "Adjuntar imagen");

    public FormularioAnuncio() {
        setLayout(new BorderLayout(8, 8));
        mensaje.setLineWrap(true);
        mensaje.setWrapStyleWord(true);

        JPanel arriba = new JPanel(new GridLayout(0, 1, 4, 4));
        arriba.add(new JLabel("Título (máximo 100 caracteres)"));
        arriba.add(titulo);
        arriba.add(new JLabel("Mensaje (máximo 2000 caracteres)"));
        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(mensaje), BorderLayout.CENTER);

        JPanel abajo = new JPanel(new BorderLayout(4, 4));
        abajo.add(new JLabel("Imagen del anuncio (opcional)"), BorderLayout.NORTH);
        abajo.add(selectorImagen, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
    }

    public String getTitulo() {
        return titulo.getText();
    }

    public String getMensaje() {
        return mensaje.getText();
    }

    public byte[] getImagen() {
        return selectorImagen.getImagen();
    }
}
package vista;

import modelo.*;
import controlador.*;
import persistencia.DatosAplicacion;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RenderizadorClub extends DefaultTableCellRenderer {
    private final HashMap<Club, ImageIcon> iconos = new HashMap<>();

    public void limpiar() {
        iconos.clear();
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean foco, int fila, int columna) {
        Club club = (Club) valor;
        super.getTableCellRendererComponent(tabla, club.getNombre(), seleccionada, foco, fila, columna);
        ImageIcon icono = iconos.get(club);
        if (icono == null) {
            icono = Imagenes.logo(club, 32);
            iconos.put(club, icono);
        }
        setIcon(icono);
        setIconTextGap(10);
        setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        return this;
    }
}
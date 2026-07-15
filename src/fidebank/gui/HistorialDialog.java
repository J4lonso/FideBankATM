package fidebank.gui;

import fidebank.modelo.Cuenta;
import fidebank.modelo.Transaccion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Muestra el historial de transacciones de la cuenta (HU-08 / HU-09).
 * Recorre la coleccion de Transaccion e invoca toString() de forma polimorfica
 * (cada subclase especializa su descripcion).
 */
public class HistorialDialog extends JDialog {

    public HistorialDialog(Frame padre, Cuenta cuenta) {
        super(padre, "Historial de Transacciones", true);
        construirInterfaz(cuenta);
    }

    private void construirInterfaz(Cuenta cuenta) {
        setSize(400, 420);
        setLocationRelativeTo(getParent());

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Historial - Cuenta " + cuenta.getNumeroCuenta(), SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        List<Transaccion> historial = cuenta.consultarHistorial();
        DefaultListModel<String> modelo = new DefaultListModel<>();
        if (historial.isEmpty()) {
            modelo.addElement("Aun no hay transacciones registradas.");
        } else {
            for (Transaccion t : historial) {
                modelo.addElement(t.toString()); // polimorfismo: toString() delega en getDescripcion()
            }
        }
        JList<String> lista = new JList<>(modelo);
        raiz.add(new JScrollPane(lista), BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.add(cerrar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
    }
}

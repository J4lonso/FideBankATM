package fidebank.gui;

import fidebank.red.ClienteRed;
import fidebank.red.Peticion;
import fidebank.red.Respuesta;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Muestra el historial de transacciones de la cuenta (HU-08 / HU-09).
 * Pide la lista al servidor; alli es donde se recorre la coleccion de Transaccion
 * invocando toString() de forma polimorfica (cada subclase especializa su descripcion).
 */
public class HistorialDialog extends JDialog {

    public HistorialDialog(Frame padre, ClienteRed clienteRed, String numeroCuenta) {
        super(padre, "Historial de Transacciones", true);
        construirInterfaz(clienteRed, numeroCuenta);
    }

    private void construirInterfaz(ClienteRed clienteRed, String numeroCuenta) {
        setSize(400, 420);
        setLocationRelativeTo(getParent());

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Historial - Cuenta " + numeroCuenta, SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        try {
            Respuesta respuesta = clienteRed.enviar(Peticion.historial(numeroCuenta));
            List<String> historial = respuesta.getHistorial();
            if (historial == null || historial.isEmpty()) {
                modelo.addElement("Aun no hay transacciones registradas.");
            } else {
                for (String linea : historial) {
                    modelo.addElement(linea);
                }
            }
        } catch (IOException ex) {
            modelo.addElement("Error de conexion con el servidor: " + ex.getMessage());
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

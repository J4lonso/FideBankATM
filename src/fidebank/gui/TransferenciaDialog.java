package fidebank.gui;

import fidebank.red.ClienteRed;
import fidebank.red.Peticion;
import fidebank.red.Respuesta;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Pantalla de transferencia entre cuentas (HU-06). Envia la peticion al servidor
 * por la conexion de red ya abierta.
 */
public class TransferenciaDialog extends JDialog {

    private final ClienteRed clienteRed;
    private final String numeroCuentaOrigen;
    private final MenuPrincipalFrame padre;
    private JTextField campoDestino;
    private JTextField campoMonto;
    private JLabel etiquetaMensaje;

    public TransferenciaDialog(MenuPrincipalFrame padre, ClienteRed clienteRed, String numeroCuentaOrigen) {
        super(padre, "Transferencia entre Cuentas", true);
        this.padre = padre;
        this.clienteRed = clienteRed;
        this.numeroCuentaOrigen = numeroCuentaOrigen;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSize(340, 400);
        setLocationRelativeTo(padre);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Transferencia entre Cuentas", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(etiqueta("Cuenta origen: " + numeroCuentaOrigen));
        centro.add(Box.createVerticalStrut(15));

        centro.add(etiqueta("Cuenta destino"));
        campoDestino = new JTextField();
        campoDestino.setMaximumSize(new Dimension(220, 30));
        campoDestino.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(campoDestino);
        centro.add(Box.createVerticalStrut(12));

        centro.add(etiqueta("Monto a transferir"));
        campoMonto = new JTextField();
        campoMonto.setMaximumSize(new Dimension(220, 30));
        campoMonto.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(campoMonto);
        centro.add(Box.createVerticalStrut(15));

        JButton transferir = new JButton("Transferir");
        transferir.setBackground(Estilos.BOTON_VERDE);
        transferir.setAlignmentX(Component.CENTER_ALIGNMENT);
        transferir.addActionListener(e -> confirmarTransferencia());
        centro.add(transferir);
        centro.add(Box.createVerticalStrut(8));

        JButton cancelar = new JButton("Cancelar");
        cancelar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelar.addActionListener(e -> dispose());
        centro.add(cancelar);

        etiquetaMensaje = new JLabel(" ", SwingConstants.CENTER);
        etiquetaMensaje.setForeground(Color.RED);
        etiquetaMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(Box.createVerticalStrut(10));
        centro.add(etiquetaMensaje);

        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        return etiqueta;
    }

    private void confirmarTransferencia() {
        String destino = campoDestino.getText().trim();
        double monto;
        try {
            monto = Double.parseDouble(campoMonto.getText().trim());
        } catch (NumberFormatException ex) {
            etiquetaMensaje.setText("Ingrese un monto valido.");
            return;
        }
        if (monto <= 0) {
            etiquetaMensaje.setText("El monto debe ser mayor a cero.");
            return;
        }
        try {
            Respuesta respuesta = clienteRed.enviar(Peticion.transferir(numeroCuentaOrigen, destino, monto));
            if (respuesta.isExitosa()) {
                padre.actualizarSaldo(respuesta.getSaldo());
                dispose();
                new ComprobanteDialog(padre, respuesta.getMensaje()).setVisible(true);
            } else {
                etiquetaMensaje.setText(respuesta.getMensaje());
            }
        } catch (IOException ex) {
            etiquetaMensaje.setText("Error de conexion con el servidor: " + ex.getMessage());
        }
    }
}

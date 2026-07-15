package fidebank.gui;

import fidebank.modelo.Cuenta;
import fidebank.modelo.Transaccion;
import fidebank.servicio.Banco;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de deposito de dinero (HU-05).
 */
public class DepositoDialog extends JDialog {

    private final Cuenta cuenta;
    private final MenuPrincipalFrame padre;
    private JTextField campoMonto;
    private JComboBox<String> comboTipo;
    private JLabel etiquetaMensaje;

    public DepositoDialog(MenuPrincipalFrame padre, Cuenta cuenta) {
        super(padre, "Deposito de Dinero", true);
        this.padre = padre;
        this.cuenta = cuenta;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSize(340, 340);
        setLocationRelativeTo(padre);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Deposito de Dinero", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel etiquetaTipo = new JLabel("Tipo de deposito");
        etiquetaTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(etiquetaTipo);
        comboTipo = new JComboBox<>(new String[]{"EFECTIVO", "CHEQUE"});
        comboTipo.setMaximumSize(new Dimension(200, 30));
        comboTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(comboTipo);
        centro.add(Box.createVerticalStrut(15));

        JLabel etiquetaMonto = new JLabel("Monto a depositar");
        etiquetaMonto.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(etiquetaMonto);
        campoMonto = new JTextField();
        campoMonto.setMaximumSize(new Dimension(200, 30));
        campoMonto.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(campoMonto);
        centro.add(Box.createVerticalStrut(15));

        JButton confirmar = new JButton("Confirmar deposito");
        confirmar.setBackground(Estilos.BOTON_VERDE);
        confirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmar.addActionListener(e -> confirmarDeposito());
        centro.add(confirmar);
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

    private void confirmarDeposito() {
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
        String tipo = (String) comboTipo.getSelectedItem();
        Transaccion transaccion = Banco.getInstancia().depositar(cuenta, monto, tipo);
        padre.actualizarSaldo();
        dispose();
        new ComprobanteDialog(padre, transaccion.generarComprobante()).setVisible(true);
    }
}

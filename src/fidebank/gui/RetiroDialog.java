package fidebank.gui;

import fidebank.excepciones.SaldoInsuficienteException;
import fidebank.modelo.Cuenta;
import fidebank.modelo.Transaccion;
import fidebank.servicio.Banco;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de retiro de fondos (HU-04).
 */
public class RetiroDialog extends JDialog {

    private final Cuenta cuenta;
    private final MenuPrincipalFrame padre;
    private JTextField campoMonto;
    private JLabel etiquetaMensaje;

    public RetiroDialog(MenuPrincipalFrame padre, Cuenta cuenta) {
        super(padre, "Retiro de Fondos", true);
        this.padre = padre;
        this.cuenta = cuenta;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSize(340, 420);
        setLocationRelativeTo(padre);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Retiro de Fondos", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel saldo = new JLabel(String.format("Saldo disponible: CRC %,.2f", cuenta.consultarSaldo()));
        saldo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(saldo);
        centro.add(Box.createVerticalStrut(15));

        JPanel montosRapidos = new JPanel(new GridLayout(2, 2, 8, 8));
        montosRapidos.setOpaque(false);
        montosRapidos.setMaximumSize(new Dimension(260, 90));
        for (int monto : new int[]{10000, 20000, 50000, 100000}) {
            JButton boton = new JButton(String.format("%,d", monto));
            boton.setBackground(Estilos.BOTON_VERDE);
            boton.addActionListener(e -> campoMonto.setText(String.valueOf(monto)));
            montosRapidos.add(boton);
        }
        montosRapidos.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(montosRapidos);
        centro.add(Box.createVerticalStrut(15));

        JLabel etiquetaOtro = new JLabel("Otro monto");
        etiquetaOtro.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(etiquetaOtro);
        campoMonto = new JTextField();
        campoMonto.setMaximumSize(new Dimension(200, 30));
        campoMonto.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(campoMonto);
        centro.add(Box.createVerticalStrut(15));

        JButton confirmar = new JButton("Confirmar retiro");
        confirmar.setBackground(Estilos.BOTON_VERDE);
        confirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmar.addActionListener(e -> confirmarRetiro());
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

    private void confirmarRetiro() {
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
            Transaccion transaccion = Banco.getInstancia().retirar(cuenta, monto);
            padre.actualizarSaldo();
            dispose();
            new ComprobanteDialog(padre, transaccion.generarComprobante()).setVisible(true);
        } catch (SaldoInsuficienteException ex) {
            etiquetaMensaje.setText(ex.getMessage());
        }
    }
}

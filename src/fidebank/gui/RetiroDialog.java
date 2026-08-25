package fidebank.gui;

import fidebank.red.ClienteRed;
import fidebank.red.Peticion;
import fidebank.red.Respuesta;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Pantalla de retiro de fondos (HU-04). Envia la peticion al servidor por la
 * conexion de red ya abierta; el servidor valida fondos y actualiza MySQL.
 */
public class RetiroDialog extends JDialog {

    private final ClienteRed clienteRed;
    private final String numeroCuenta;
    private final MenuPrincipalFrame padre;
    private JTextField campoMonto;
    private JLabel etiquetaMensaje;

    public RetiroDialog(MenuPrincipalFrame padre, ClienteRed clienteRed, String numeroCuenta, double saldoActual) {
        super(padre, "Retiro de Fondos", true);
        this.padre = padre;
        this.clienteRed = clienteRed;
        this.numeroCuenta = numeroCuenta;
        construirInterfaz(saldoActual);
    }

    private void construirInterfaz(double saldoActual) {
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

        JLabel saldo = new JLabel(String.format("Saldo disponible: CRC %,.2f", saldoActual));
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
            Respuesta respuesta = clienteRed.enviar(Peticion.retirar(numeroCuenta, monto));
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

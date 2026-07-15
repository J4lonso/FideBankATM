package fidebank.gui;

import fidebank.excepciones.CuentaBloqueadaException;
import fidebank.excepciones.CuentaNoEncontradaException;
import fidebank.excepciones.PinInvalidoException;
import fidebank.modelo.Cuenta;
import fidebank.servicio.Banco;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de ingreso del cajero automatico (HU-02, HU-10).
 * El cliente ingresa su numero de cuenta y PIN para acceder a sus tramites bancarios.
 */
public class LoginFrame extends JFrame {

    private JTextField campoCuenta;
    private JPasswordField campoPin;
    private JLabel etiquetaMensaje;

    public LoginFrame() {
        super("FideBank ATM - Ingreso");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("FideBank ATM - Ingreso", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel bienvenida = new JLabel("Bienvenido(a). Ingrese sus datos.");
        bienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(bienvenida);
        centro.add(Box.createVerticalStrut(15));

        centro.add(etiquetaCampo("Numero de cuenta"));
        campoCuenta = new JTextField();
        limitarAncho(campoCuenta);
        centro.add(campoCuenta);
        centro.add(Box.createVerticalStrut(10));

        centro.add(etiquetaCampo("PIN"));
        campoPin = new JPasswordField();
        limitarAncho(campoPin);
        centro.add(campoPin);
        centro.add(Box.createVerticalStrut(15));

        JButton botonIngresar = new JButton("Ingresar");
        botonIngresar.setBackground(Estilos.BOTON_VERDE);
        botonIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonIngresar.addActionListener(e -> intentarIngreso());
        centro.add(botonIngresar);
        centro.add(Box.createVerticalStrut(10));

        JButton botonAbrirCuenta = new JButton("Abrir cuenta nueva");
        botonAbrirCuenta.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonAbrirCuenta.addActionListener(e -> new AperturaCuentaDialog(this).setVisible(true));
        centro.add(botonAbrirCuenta);
        centro.add(Box.createVerticalStrut(10));

        etiquetaMensaje = new JLabel(" ", SwingConstants.CENTER);
        etiquetaMensaje.setForeground(Color.RED);
        etiquetaMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(etiquetaMensaje);

        centro.add(Box.createVerticalStrut(20));
        JLabel demo = new JLabel("<html><center>Cuentas demo:<br>100000 / PIN 1234<br>100001 / PIN 5678</center></html>");
        demo.setForeground(Color.GRAY);
        demo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(demo);

        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);

        getRootPane().setDefaultButton(botonIngresar);
    }

    private JLabel etiquetaCampo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        return etiqueta;
    }

    private void limitarAncho(JComponent componente) {
        componente.setMaximumSize(new Dimension(260, 32));
        componente.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void intentarIngreso() {
        String numeroCuenta = campoCuenta.getText().trim();
        String pin = new String(campoPin.getPassword());

        if (numeroCuenta.isEmpty() || pin.isEmpty()) {
            etiquetaMensaje.setText("Debe ingresar cuenta y PIN.");
            return;
        }

        try {
            Cuenta cuenta = Banco.getInstancia().autenticar(numeroCuenta, pin);
            etiquetaMensaje.setText(" ");
            new MenuPrincipalFrame(cuenta).setVisible(true);
            dispose();
        } catch (PinInvalidoException ex) {
            etiquetaMensaje.setText(ex.getMessage());
            campoPin.setText("");
        } catch (CuentaBloqueadaException | CuentaNoEncontradaException ex) {
            etiquetaMensaje.setText(ex.getMessage());
            campoPin.setText("");
        }
    }
}

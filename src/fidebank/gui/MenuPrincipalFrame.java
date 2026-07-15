package fidebank.gui;

import fidebank.modelo.Cuenta;
import fidebank.persistencia.Persistencia;
import fidebank.servicio.Banco;

import javax.swing.*;
import java.awt.*;

/**
 * Menu principal de transacciones del cajero automatico.
 * Historia de usuario relacionada: HU-03 (consulta de saldo) y punto de acceso al resto de HUs.
 */
public class MenuPrincipalFrame extends JFrame {

    private final Cuenta cuenta;
    private JLabel etiquetaSaldo;

    public MenuPrincipalFrame(Cuenta cuenta) {
        super("FideBank ATM - Menu Principal");
        this.cuenta = cuenta;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(360, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Menu Principal", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        etiquetaSaldo = new JLabel();
        etiquetaSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);
        actualizarSaldo();
        centro.add(etiquetaSaldo);
        centro.add(Box.createVerticalStrut(15));

        centro.add(boton("Retirar fondos", e -> new RetiroDialog(this, cuenta).setVisible(true)));
        centro.add(Box.createVerticalStrut(8));
        centro.add(boton("Depositar dinero", e -> new DepositoDialog(this, cuenta).setVisible(true)));
        centro.add(Box.createVerticalStrut(8));
        centro.add(boton("Transferir entre cuentas", e -> new TransferenciaDialog(this, cuenta).setVisible(true)));
        centro.add(Box.createVerticalStrut(8));
        centro.add(boton("Ver historial", e -> new HistorialDialog(this, cuenta).setVisible(true)));
        centro.add(Box.createVerticalStrut(20));

        JButton salir = new JButton("Salir");
        salir.setAlignmentX(Component.CENTER_ALIGNMENT);
        salir.addActionListener(e -> {
            Persistencia.guardar(Banco.getInstancia());
            dispose();
            new LoginFrame().setVisible(true);
        });
        centro.add(salir);

        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    /** HU-03: refresca el saldo disponible mostrado en pantalla. */
    public void actualizarSaldo() {
        etiquetaSaldo.setText(String.format(
            "<html><center>Cuenta: %s<br>Saldo disponible: CRC %,.2f</center></html>",
            cuenta.getNumeroCuenta(), cuenta.consultarSaldo()));
    }

    private JButton boton(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setBackground(Estilos.BOTON_CLARO);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(280, 42));
        boton.addActionListener(accion);
        return boton;
    }
}

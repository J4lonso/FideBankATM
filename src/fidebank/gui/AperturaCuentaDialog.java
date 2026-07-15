package fidebank.gui;

import fidebank.modelo.Cuenta;
import fidebank.servicio.Banco;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de apertura de cuentas de clientes (HU-01).
 */
public class AperturaCuentaDialog extends JDialog {

    private JTextField campoNombre;
    private JTextField campoCedula;
    private JTextField campoTelefono;
    private JTextField campoCorreo;
    private JComboBox<String> comboTipo;
    private JTextField campoDeposito;
    private JPasswordField campoPin;
    private JLabel etiquetaMensaje;

    public AperturaCuentaDialog(Frame padre) {
        super(padre, "Apertura de Cuenta", true);
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSize(360, 520);
        setLocationRelativeTo(getParent());

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Apertura de Cuenta", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        campoNombre = campoConEtiqueta(centro, "Nombre completo");
        campoCedula = campoConEtiqueta(centro, "Cedula");
        campoTelefono = campoConEtiqueta(centro, "Telefono");
        campoCorreo = campoConEtiqueta(centro, "Correo");

        JLabel etiquetaTipo = new JLabel("Tipo de cuenta");
        etiquetaTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(etiquetaTipo);
        comboTipo = new JComboBox<>(new String[]{"AHORROS", "CORRIENTE"});
        comboTipo.setMaximumSize(new Dimension(220, 30));
        comboTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(comboTipo);
        centro.add(Box.createVerticalStrut(10));

        campoDeposito = campoConEtiqueta(centro, "Deposito inicial");

        JLabel etiquetaPin = new JLabel("Defina un PIN (4 digitos)");
        etiquetaPin.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(etiquetaPin);
        campoPin = new JPasswordField();
        campoPin.setMaximumSize(new Dimension(220, 30));
        campoPin.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(campoPin);
        centro.add(Box.createVerticalStrut(15));

        JButton crear = new JButton("Crear cuenta");
        crear.setBackground(Estilos.BOTON_VERDE);
        crear.setAlignmentX(Component.CENTER_ALIGNMENT);
        crear.addActionListener(e -> crearCuenta());
        centro.add(crear);
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

        raiz.add(new JScrollPane(centro), BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JTextField campoConEtiqueta(JPanel contenedor, String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenedor.add(etiqueta);
        JTextField campo = new JTextField();
        campo.setMaximumSize(new Dimension(220, 30));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenedor.add(campo);
        contenedor.add(Box.createVerticalStrut(8));
        return campo;
    }

    private void crearCuenta() {
        String nombre = campoNombre.getText().trim();
        String cedula = campoCedula.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String correo = campoCorreo.getText().trim();
        String tipo = (String) comboTipo.getSelectedItem();
        String pin = new String(campoPin.getPassword());

        if (nombre.isEmpty() || cedula.isEmpty() || pin.length() != 4) {
            etiquetaMensaje.setText("Complete nombre, cedula y un PIN de 4 digitos.");
            return;
        }
        double deposito;
        try {
            deposito = campoDeposito.getText().trim().isEmpty()
                ? 0 : Double.parseDouble(campoDeposito.getText().trim());
        } catch (NumberFormatException ex) {
            etiquetaMensaje.setText("El deposito inicial debe ser numerico.");
            return;
        }

        Cuenta cuenta = Banco.getInstancia().abrirCuenta(
            nombre, cedula, "", telefono, correo, tipo, deposito, pin);

        JOptionPane.showMessageDialog(this,
            "Cuenta creada con exito.\nNumero de cuenta: " + cuenta.getNumeroCuenta()
                + "\nGuarde este numero junto con su PIN.");
        dispose();
    }
}

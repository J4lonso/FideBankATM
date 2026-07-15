package fidebank.gui;

import fidebank.modelo.Comprobante;

import javax.swing.*;
import java.awt.*;

/**
 * Muestra el comprobante de una transaccion y permite "imprimirlo" (HU-07).
 */
public class ComprobanteDialog extends JDialog {

    public ComprobanteDialog(Frame padre, Comprobante comprobante) {
        super(padre, "Comprobante", true);
        construirInterfaz(comprobante);
    }

    private void construirInterfaz(Comprobante comprobante) {
        setSize(340, 380);
        setLocationRelativeTo(getParent());

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(Estilos.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Comprobante", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Estilos.NAVY);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        JTextArea texto = new JTextArea(comprobante.generar());
        texto.setEditable(false);
        texto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        texto.setBackground(Estilos.FONDO);
        raiz.add(new JScrollPane(texto), BorderLayout.CENTER);

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        JButton imprimir = new JButton("Imprimir");
        imprimir.setBackground(Estilos.BOTON_VERDE);
        imprimir.addActionListener(e -> {
            comprobante.imprimir();
            JOptionPane.showMessageDialog(this, "Comprobante enviado a la impresora.");
        });
        JButton finalizar = new JButton("Finalizar");
        finalizar.addActionListener(e -> dispose());
        botones.add(imprimir);
        botones.add(finalizar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
    }
}

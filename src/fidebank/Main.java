package fidebank;

import fidebank.gui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada del CLIENTE de la aplicacion de cajeros automaticos FideBank.
 *
 * Este programa ya NO contiene la logica de negocio ni el acceso a datos: es la
 * interfaz grafica de un cajero fisico que se conecta por red al servidor
 * (fidebank.red.ServidorFideBank), el cual debe estar corriendo antes de abrir esta
 * ventana. Para levantar el servidor: `java fidebank.red.ServidorFideBank`.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, se usa el look and feel por defecto de Swing.
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

package fidebank;

import fidebank.gui.LoginFrame;
import fidebank.persistencia.Persistencia;
import fidebank.servicio.Banco;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion de cajeros automaticos FideBank.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, se usa el look and feel por defecto de Swing.
        }

        // Carga el estado previo (serializacion) o crea datos de demostracion.
        Banco banco = Persistencia.cargar();
        if (banco.cantidadCuentas() == 0) {
            crearDatosDemo(banco);
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    /** Crea un par de cuentas de ejemplo para poder probar el flujo del cajero. */
    private static void crearDatosDemo(Banco banco) {
        banco.abrirCuenta("Ana Rodriguez", "1-1111-1111", "San Jose", "8888-0001",
                "ana@correo.com", "AHORROS", 350000, "1234");
        banco.abrirCuenta("Luis Fernandez", "2-2222-2222", "Heredia", "8888-0002",
                "luis@correo.com", "CORRIENTE", 500000, "5678");
    }
}

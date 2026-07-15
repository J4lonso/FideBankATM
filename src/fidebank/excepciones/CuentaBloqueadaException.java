package fidebank.excepciones;

/**
 * Se lanza cuando una cuenta ha superado el numero maximo de intentos de PIN fallidos.
 * Historia de usuario relacionada: HU-10.
 */
public class CuentaBloqueadaException extends Exception {

    private static final long serialVersionUID = 1L;

    public CuentaBloqueadaException(String mensaje) {
        super(mensaje);
    }
}

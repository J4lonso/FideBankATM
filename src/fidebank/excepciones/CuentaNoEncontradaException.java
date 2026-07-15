package fidebank.excepciones;

/**
 * Se lanza cuando se hace referencia a un numero de cuenta que no existe en el banco.
 * Historia de usuario relacionada: HU-06.
 */
public class CuentaNoEncontradaException extends Exception {

    private static final long serialVersionUID = 1L;

    public CuentaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}

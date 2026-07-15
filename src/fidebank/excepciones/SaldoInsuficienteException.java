package fidebank.excepciones;

/**
 * Se lanza cuando se intenta retirar o transferir un monto mayor al saldo disponible.
 * Historia de usuario relacionada: HU-04, HU-06.
 */
public class SaldoInsuficienteException extends Exception {

    private static final long serialVersionUID = 1L;

    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}

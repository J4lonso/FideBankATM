package fidebank.excepciones;

/**
 * Se lanza cuando el PIN ingresado por el cliente no coincide con el registrado.
 * Historia de usuario relacionada: HU-02.
 */
public class PinInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;
    private final int intentosRestantes;

    public PinInvalidoException(String mensaje, int intentosRestantes) {
        super(mensaje);
        this.intentosRestantes = intentosRestantes;
    }

    public int getIntentosRestantes() {
        return intentosRestantes;
    }
}

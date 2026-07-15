package fidebank.modelo;

import fidebank.excepciones.SaldoInsuficienteException;

/**
 * Transaccion de transferencia entre cuentas. Hereda de Transaccion.
 * Historia de usuario relacionada: HU-06.
 */
public class Transferencia extends Transaccion {

    private static final long serialVersionUID = 1L;

    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;

    public Transferencia(double monto, Cuenta cuentaOrigen, Cuenta cuentaDestino) {
        super(monto, cuentaOrigen.getNumeroCuenta());
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
    }

    /** Valida que la cuenta destino exista y sea distinta de la de origen. */
    public boolean validarCuentaDestino() {
        return cuentaDestino != null
            && !cuentaDestino.getNumeroCuenta().equals(cuentaOrigen.getNumeroCuenta());
    }

    @Override
    public boolean ejecutar() throws SaldoInsuficienteException {
        cuentaOrigen.debitar(monto);
        cuentaDestino.acreditar(monto);
        cuentaOrigen.agregarTransaccion(this);
        cuentaDestino.agregarTransaccion(this);
        return true;
    }

    @Override
    public String getDescripcion() {
        return "Transferencia a cuenta " + cuentaDestino.getNumeroCuenta();
    }

    public String getCuentaDestino() {
        return cuentaDestino.getNumeroCuenta();
    }
}

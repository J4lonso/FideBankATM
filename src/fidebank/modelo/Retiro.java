package fidebank.modelo;

import fidebank.excepciones.SaldoInsuficienteException;

/**
 * Transaccion de retiro de fondos. Hereda de Transaccion.
 * Historia de usuario relacionada: HU-04.
 */
public class Retiro extends Transaccion {

    private static final long serialVersionUID = 1L;

    private Cuenta cuenta;

    public Retiro(double monto, Cuenta cuenta) {
        super(monto, cuenta.getNumeroCuenta());
        this.cuenta = cuenta;
    }

    /** Verifica que la cuenta tenga fondos suficientes antes de ejecutar. */
    public boolean verificarFondos() {
        return cuenta.consultarSaldo() >= monto;
    }

    @Override
    public boolean ejecutar() throws SaldoInsuficienteException {
        cuenta.debitar(monto);
        cuenta.agregarTransaccion(this);
        return true;
    }

    @Override
    public String getDescripcion() {
        return "Retiro de fondos";
    }
}

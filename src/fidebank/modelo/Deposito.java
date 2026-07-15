package fidebank.modelo;

/**
 * Transaccion de deposito de dinero. Hereda de Transaccion.
 * Historia de usuario relacionada: HU-05.
 */
public class Deposito extends Transaccion {

    private static final long serialVersionUID = 1L;

    private Cuenta cuenta;
    private String tipoDeposito; // EFECTIVO, CHEQUE

    public Deposito(double monto, Cuenta cuenta, String tipoDeposito) {
        super(monto, cuenta.getNumeroCuenta());
        this.cuenta = cuenta;
        this.tipoDeposito = tipoDeposito;
    }

    public void registrarDeposito() {
        cuenta.agregarTransaccion(this);
    }

    @Override
    public boolean ejecutar() {
        cuenta.acreditar(monto);
        registrarDeposito();
        return true;
    }

    @Override
    public String getDescripcion() {
        return "Deposito (" + tipoDeposito + ")";
    }
}

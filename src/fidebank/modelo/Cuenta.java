package fidebank.modelo;

import fidebank.excepciones.CuentaBloqueadaException;
import fidebank.excepciones.PinInvalidoException;
import fidebank.excepciones.SaldoInsuficienteException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una cuenta bancaria de un cliente de FideBank.
 * Historias de usuario relacionadas: HU-01, HU-02, HU-03, HU-08, HU-10.
 */
public class Cuenta implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int MAX_INTENTOS_PIN = 3;

    private String numeroCuenta;
    private String tipoCuenta;
    private double saldo;
    private String pin;
    private LocalDate fechaApertura;
    private String estado; // ACTIVA, BLOQUEADA
    private int intentosFallidos;

    // Coleccion: historial de transacciones de la cuenta (HU-08, HU-09)
    private List<Transaccion> historial;

    public Cuenta(String numeroCuenta, String tipoCuenta, double saldoInicial, String pin) {
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldoInicial;
        this.pin = pin;
        this.fechaApertura = LocalDate.now();
        this.estado = "ACTIVA";
        this.intentosFallidos = 0;
        this.historial = new ArrayList<>();
    }

    /** HU-02 / HU-10: valida el PIN y controla el bloqueo tras varios intentos fallidos. */
    public boolean validarPin(String pinIngresado) throws PinInvalidoException, CuentaBloqueadaException {
        if ("BLOQUEADA".equals(estado)) {
            throw new CuentaBloqueadaException(
                "La cuenta " + numeroCuenta + " se encuentra bloqueada. Contacte a su sucursal.");
        }
        if (pin.equals(pinIngresado)) {
            intentosFallidos = 0;
            return true;
        }
        intentosFallidos++;
        int restantes = MAX_INTENTOS_PIN - intentosFallidos;
        if (restantes <= 0) {
            estado = "BLOQUEADA";
            throw new CuentaBloqueadaException(
                "PIN incorrecto. La cuenta " + numeroCuenta + " ha sido bloqueada por seguridad.");
        }
        throw new PinInvalidoException("PIN incorrecto. Intentos restantes: " + restantes, restantes);
    }

    /** HU-03: consulta de saldo disponible. */
    public double consultarSaldo() {
        return saldo;
    }

    /** Usado por Retiro/Transferencia para validar y descontar fondos. */
    public void debitar(double monto) throws SaldoInsuficienteException {
        if (monto > saldo) {
            throw new SaldoInsuficienteException(
                "Saldo insuficiente en la cuenta " + numeroCuenta + ". Disponible: " + saldo);
        }
        saldo -= monto;
    }

    /** Usado por Deposito/Transferencia para acreditar fondos. */
    public void acreditar(double monto) {
        saldo += monto;
    }

    public void actualizarSaldo(double nuevoSaldo) {
        this.saldo = nuevoSaldo;
    }

    /** HU-09: registra cada transaccion ejecutada sobre esta cuenta. */
    public void agregarTransaccion(Transaccion t) {
        historial.add(t);
    }

    /** HU-08: consulta el historial completo de transacciones. */
    public List<Transaccion> consultarHistorial() {
        return historial;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * Expone el PIN solo para uso interno del servidor (persistencia en MySQL).
     * No se envia nunca al cliente a traves del protocolo de red (ver clase Respuesta).
     */
    public String getPin() {
        return pin;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    @Override
    public String toString() {
        return numeroCuenta + " (" + tipoCuenta + ") - saldo: " + saldo;
    }
}

package fidebank.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Clase abstracta que representa una transaccion bancaria generica.
 * De ella heredan Retiro, Deposito y Transferencia (herencia).
 * El metodo ejecutar() es sobrescrito por cada subclase (polimorfismo).
 */
public abstract class Transaccion implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int contador = 1000;

    protected int idTransaccion;
    protected LocalDateTime fecha;
    protected double monto;
    protected String numeroCuenta;

    public Transaccion(double monto, String numeroCuenta) {
        this.idTransaccion = contador++;
        this.fecha = LocalDateTime.now();
        this.monto = monto;
        this.numeroCuenta = numeroCuenta;
    }

    /** Cada subclase implementa su propia logica de negocio (polimorfismo). */
    public abstract boolean ejecutar() throws Exception;

    /** Genera el comprobante asociado a esta transaccion. */
    public Comprobante generarComprobante() {
        return new Comprobante(this);
    }

    /** Descripcion legible usada en comprobantes e historial; cada subclase la especializa. */
    public abstract String getDescripcion();

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getMonto() {
        return monto;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    @Override
    public String toString() {
        return "#" + idTransaccion + " " + fecha + " - " + getDescripcion() + " - CRC " + monto;
    }
}

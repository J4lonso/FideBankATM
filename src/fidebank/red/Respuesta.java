package fidebank.red;

import java.io.Serializable;
import java.util.List;

/**
 * Objeto que el servidor devuelve al cliente (cajero) a traves del socket.
 * exitosa indica si la operacion se pudo completar; en caso contrario, mensaje
 * trae el motivo (mismo texto que hubiera lanzado la excepcion en el servidor).
 */
public class Respuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean exitosa;
    private String mensaje;
    private String numeroCuenta;
    private double saldo;
    private List<String> historial;

    public static Respuesta ok(String mensaje) {
        Respuesta r = new Respuesta();
        r.exitosa = true;
        r.mensaje = mensaje;
        return r;
    }

    public static Respuesta error(String mensaje) {
        Respuesta r = new Respuesta();
        r.exitosa = false;
        r.mensaje = mensaje;
        return r;
    }

    public Respuesta conCuenta(String numeroCuenta, double saldo) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        return this;
    }

    public Respuesta conHistorial(List<String> historial) {
        this.historial = historial;
        return this;
    }

    public boolean isExitosa() { return exitosa; }
    public String getMensaje() { return mensaje; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public double getSaldo() { return saldo; }
    public List<String> getHistorial() { return historial; }
}

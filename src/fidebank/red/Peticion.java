package fidebank.red;

import java.io.Serializable;

/**
 * Objeto que viaja del cliente (cajero) al servidor a traves del socket.
 * Se serializa con ObjectOutputStream y se reconstruye en el servidor con ObjectInputStream.
 */
public class Peticion implements Serializable {

    private static final long serialVersionUID = 1L;

    private TipoOperacion tipo;
    private String numeroCuenta;
    private String pin;
    private double monto;
    private String cuentaDestino;
    private String tipoDeposito;

    // Datos usados solo para ABRIR_CUENTA
    private String nombre;
    private String cedula;
    private String telefono;
    private String correo;
    private String tipoCuenta;

    public static Peticion autenticar(String numeroCuenta, String pin) {
        Peticion p = new Peticion();
        p.tipo = TipoOperacion.AUTENTICAR;
        p.numeroCuenta = numeroCuenta;
        p.pin = pin;
        return p;
    }

    public static Peticion consultarSaldo(String numeroCuenta) {
        Peticion p = new Peticion();
        p.tipo = TipoOperacion.CONSULTAR_SALDO;
        p.numeroCuenta = numeroCuenta;
        return p;
    }

    public static Peticion retirar(String numeroCuenta, double monto) {
        Peticion p = new Peticion();
        p.tipo = TipoOperacion.RETIRAR;
        p.numeroCuenta = numeroCuenta;
        p.monto = monto;
        return p;
    }

    public static Peticion depositar(String numeroCuenta, double monto, String tipoDeposito) {
        Peticion p = new Peticion();
        p.tipo = TipoOperacion.DEPOSITAR;
        p.numeroCuenta = numeroCuenta;
        p.monto = monto;
        p.tipoDeposito = tipoDeposito;
        return p;
    }

    public static Peticion transferir(String numeroCuenta, String cuentaDestino, double monto) {
        Peticion p = new Peticion();
        p.tipo = TipoOperacion.TRANSFERIR;
        p.numeroCuenta = numeroCuenta;
        p.cuentaDestino = cuentaDestino;
        p.monto = monto;
        return p;
    }

    public static Peticion historial(String numeroCuenta) {
        Peticion p = new Peticion();
        p.tipo = TipoOperacion.HISTORIAL;
        p.numeroCuenta = numeroCuenta;
        return p;
    }

    public static Peticion abrirCuenta(String nombre, String cedula, String telefono, String correo,
                                        String tipoCuenta, double depositoInicial, String pin) {
        Peticion p = new Peticion();
        p.tipo = TipoOperacion.ABRIR_CUENTA;
        p.nombre = nombre;
        p.cedula = cedula;
        p.telefono = telefono;
        p.correo = correo;
        p.tipoCuenta = tipoCuenta;
        p.monto = depositoInicial;
        p.pin = pin;
        return p;
    }

    public TipoOperacion getTipo() { return tipo; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public String getPin() { return pin; }
    public double getMonto() { return monto; }
    public String getCuentaDestino() { return cuentaDestino; }
    public String getTipoDeposito() { return tipoDeposito; }
    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getTipoCuenta() { return tipoCuenta; }
}

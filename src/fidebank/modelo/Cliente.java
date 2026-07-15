package fidebank.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un cliente de FideBank.
 * Historia de usuario relacionada: HU-01 (apertura de cuenta).
 */
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idCliente;
    private String nombre;
    private String cedula;
    private String direccion;
    private String telefono;
    private String correo;
    private List<Cuenta> cuentas;

    public Cliente(int idCliente, String nombre, String cedula, String direccion,
                   String telefono, String correo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.cedula = cedula;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.cuentas = new ArrayList<>();
    }

    public void registrarCliente() {
        // Punto de extension: aqui se podria validar formato de cedula, correo, etc.
    }

    public void actualizarDatos(String direccion, String telefono, String correo) {
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
    }

    public List<Cuenta> consultarCuentas() {
        return cuentas;
    }

    public void agregarCuenta(Cuenta cuenta) {
        cuentas.add(cuenta);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public String toString() {
        return nombre + " (cedula " + cedula + ")";
    }
}

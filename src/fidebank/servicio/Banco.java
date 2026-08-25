package fidebank.servicio;

import fidebank.excepciones.CuentaBloqueadaException;
import fidebank.excepciones.CuentaNoEncontradaException;
import fidebank.excepciones.PinInvalidoException;
import fidebank.excepciones.SaldoInsuficienteException;
import fidebank.modelo.Cliente;
import fidebank.modelo.Cuenta;
import fidebank.modelo.Deposito;
import fidebank.modelo.Retiro;
import fidebank.modelo.Transaccion;
import fidebank.modelo.Transferencia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Capa de servicio que actua como fachada del banco: administra clientes y cuentas
 * usando colecciones (Map/List) y expone las operaciones de negocio del cajero automatico.
 *
 * Historias de usuario cubiertas: HU-01 a HU-10.
 */
public class Banco implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Banco instancia;

    private Map<String, Cliente> clientes;   // clave: cedula
    private Map<String, Cuenta> cuentas;     // clave: numeroCuenta
    private int siguienteIdCliente;
    private int siguienteNumeroCuenta;

    private Banco() {
        clientes = new LinkedHashMap<>();
        cuentas = new LinkedHashMap<>();
        siguienteIdCliente = 1;
        siguienteNumeroCuenta = 100000;
    }

    public static synchronized Banco getInstancia() {
        if (instancia == null) {
            instancia = new Banco();
        }
        return instancia;
    }

    /** Permite reemplazar la instancia en memoria al restaurar desde un archivo serializado. */
    public static synchronized void setInstancia(Banco banco) {
        instancia = banco;
    }

    // ---------- HU-01: Apertura de cuentas ----------
    public Cuenta abrirCuenta(String nombre, String cedula, String direccion, String telefono,
                               String correo, String tipoCuenta, double depositoInicial, String pin) {
        Cliente cliente = clientes.get(cedula);
        if (cliente == null) {
            cliente = new Cliente(siguienteIdCliente++, nombre, cedula, direccion, telefono, correo);
            cliente.registrarCliente();
            clientes.put(cedula, cliente);
        }
        String numeroCuenta = String.valueOf(siguienteNumeroCuenta++);
        Cuenta cuenta = new Cuenta(numeroCuenta, tipoCuenta, depositoInicial, pin);
        cliente.agregarCuenta(cuenta);
        cuentas.put(numeroCuenta, cuenta);
        return cuenta;
    }

    // ---------- HU-02 / HU-10: Autenticacion con PIN ----------
    public Cuenta autenticar(String numeroCuenta, String pin)
            throws CuentaNoEncontradaException, PinInvalidoException, CuentaBloqueadaException {
        Cuenta cuenta = buscarCuenta(numeroCuenta);
        cuenta.validarPin(pin);
        return cuenta;
    }

    // ---------- HU-04: Retiro de fondos ----------
    public Transaccion retirar(Cuenta cuenta, double monto) throws SaldoInsuficienteException {
        Retiro retiro = new Retiro(monto, cuenta);
        if (!retiro.verificarFondos()) {
            throw new SaldoInsuficienteException(
                "Fondos insuficientes para retirar CRC " + monto + " de la cuenta " + cuenta.getNumeroCuenta());
        }
        retiro.ejecutar();
        return retiro;
    }

    // ---------- HU-05: Deposito de dinero ----------
    public Transaccion depositar(Cuenta cuenta, double monto, String tipoDeposito) {
        Deposito deposito = new Deposito(monto, cuenta, tipoDeposito);
        deposito.ejecutar();
        return deposito;
    }

    // ---------- HU-06: Transferencia entre cuentas ----------
    public Transaccion transferir(Cuenta origen, String numeroCuentaDestino, double monto)
            throws SaldoInsuficienteException, CuentaNoEncontradaException {
        Cuenta destino = buscarCuenta(numeroCuentaDestino);
        Transferencia transferencia = new Transferencia(monto, origen, destino);
        if (!transferencia.validarCuentaDestino()) {
            throw new CuentaNoEncontradaException("La cuenta destino no es valida.");
        }
        transferencia.ejecutar();
        return transferencia;
    }

    // ---------- HU-08: Historial de transacciones ----------
    public List<Transaccion> obtenerHistorial(Cuenta cuenta) {
        return cuenta.consultarHistorial();
    }

    public Cuenta buscarCuenta(String numeroCuenta) throws CuentaNoEncontradaException {
        Cuenta cuenta = cuentas.get(numeroCuenta);
        if (cuenta == null) {
            throw new CuentaNoEncontradaException("No existe la cuenta " + numeroCuenta);
        }
        return cuenta;
    }

    /** Igual que buscarCuenta, pero retorna null en vez de lanzar excepcion (uso interno). */
    public Cuenta buscarCuentaSilenciosa(String numeroCuenta) {
        return cuentas.get(numeroCuenta);
    }

    public List<Cuenta> listarCuentas() {
        return new ArrayList<>(cuentas.values());
    }

    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientes.values());
    }

    public int cantidadCuentas() {
        return cuentas.size();
    }

    /**
     * Inserta en memoria un Cliente y una Cuenta ya existentes (por ejemplo, cargados desde
     * MySQL al iniciar el servidor) sin pasar por abrirCuenta(), preservando sus datos originales.
     * Ajusta los contadores internos para que las proximas cuentas/clientes nuevos no choquen
     * con los identificadores ya usados.
     */
    public void registrarDesdePersistencia(Cliente cliente, Cuenta cuenta) {
        clientes.putIfAbsent(cliente.getCedula(), cliente);
        clientes.get(cliente.getCedula()).agregarCuenta(cuenta);
        cuentas.put(cuenta.getNumeroCuenta(), cuenta);

        if (cliente.getIdCliente() >= siguienteIdCliente) {
            siguienteIdCliente = cliente.getIdCliente() + 1;
        }
        try {
            int numero = Integer.parseInt(cuenta.getNumeroCuenta());
            if (numero >= siguienteNumeroCuenta) {
                siguienteNumeroCuenta = numero + 1;
            }
        } catch (NumberFormatException ignorado) {
            // Numeros de cuenta no numericos no afectan el contador.
        }
    }
}

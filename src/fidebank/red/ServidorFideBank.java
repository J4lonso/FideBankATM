package fidebank.red;

import fidebank.excepciones.CuentaBloqueadaException;
import fidebank.excepciones.CuentaNoEncontradaException;
import fidebank.excepciones.PinInvalidoException;
import fidebank.excepciones.SaldoInsuficienteException;
import fidebank.modelo.Cuenta;
import fidebank.modelo.Transaccion;
import fidebank.persistencia.BancoDAO;
import fidebank.servicio.Banco;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor central de FideBank. Escucha conexiones TCP de los cajeros automaticos
 * (clientes) y atiende cada una en un hilo independiente (concurrencia), delegando
 * las operaciones de negocio en Banco y persistiendolas en MySQL via BancoDAO.
 *
 * Este componente es la evidencia de "Redes" pedida en la presentacion final: la
 * arquitectura pasa de ser un unico programa de escritorio a un modelo cliente-servidor
 * real, en el que varios cajeros pueden conectarse simultaneamente al mismo banco.
 */
public class ServidorFideBank {

    public static final int PUERTO_POR_DEFECTO = 5050;

    private final int puerto;
    private final ExecutorService pool;
    private volatile boolean activo;
    private ServerSocket serverSocket;

    public ServidorFideBank(int puerto) {
        this.puerto = puerto;
        this.pool = Executors.newCachedThreadPool();
    }

    public static void main(String[] args) throws Exception {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : PUERTO_POR_DEFECTO;

        // Carga las cuentas existentes desde MySQL antes de aceptar conexiones.
        BancoDAO dao = new BancoDAO();
        dao.crearTablasSiNoExisten();
        Banco banco = Banco.getInstancia();
        dao.cargarEnMemoria(banco);

        if (banco.cantidadCuentas() == 0) {
            crearDatosDemo(banco, dao);
        }

        ServidorFideBank servidor = new ServidorFideBank(puerto);
        System.out.println("Servidor FideBank escuchando en el puerto " + puerto + "...");
        servidor.iniciar();
    }

    /** Si la base esta vacia (primera vez), siembra un par de cuentas de ejemplo en MySQL. */
    private static void crearDatosDemo(Banco banco, BancoDAO dao) {
        Cuenta c1 = banco.abrirCuenta("Ana Rodriguez", "1-1111-1111", "San Jose", "8888-0001",
            "ana@correo.com", "AHORROS", 350000, "1234");
        dao.guardarCliente("Ana Rodriguez", "1-1111-1111", "8888-0001", "ana@correo.com");
        dao.guardarCuenta(c1, "1-1111-1111");

        Cuenta c2 = banco.abrirCuenta("Luis Fernandez", "2-2222-2222", "Heredia", "8888-0002",
            "luis@correo.com", "CORRIENTE", 500000, "5678");
        dao.guardarCliente("Luis Fernandez", "2-2222-2222", "8888-0002", "luis@correo.com");
        dao.guardarCuenta(c2, "2-2222-2222");

        System.out.println("Datos de demostracion creados en MySQL: cuentas "
            + c1.getNumeroCuenta() + " y " + c2.getNumeroCuenta());
    }

    public void iniciar() throws IOException {
        activo = true;
        serverSocket = new ServerSocket(puerto);
        while (activo) {
            try {
                Socket socketCliente = serverSocket.accept();
                pool.submit(new ManejadorCliente(socketCliente));
            } catch (IOException e) {
                if (activo) {
                    System.err.println("Error aceptando conexion: " + e.getMessage());
                }
            }
        }
    }

    public void detener() throws IOException {
        activo = false;
        pool.shutdownNow();
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    /** Atiende una conexion de cajero: un hilo por cliente conectado. */
    private static class ManejadorCliente implements Runnable {

        private final Socket socket;
        private final BancoDAO dao;

        ManejadorCliente(Socket socket) {
            this.socket = socket;
            this.dao = new BancoDAO();
        }

        @Override
        public void run() {
            try (ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

                while (true) {
                    Object obj = entrada.readObject();
                    if (!(obj instanceof Peticion)) {
                        continue;
                    }
                    Respuesta respuesta = procesar((Peticion) obj);
                    salida.writeObject(respuesta);
                    salida.flush();
                }
            } catch (IOException | ClassNotFoundException e) {
                // El cliente cerro la conexion (por ejemplo, al salir del menu principal).
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        private Respuesta procesar(Peticion peticion) {
            Banco banco = Banco.getInstancia();
            try {
                switch (peticion.getTipo()) {
                    case AUTENTICAR:
                        return manejarAutenticar(banco, peticion);
                    case ABRIR_CUENTA:
                        return manejarAbrirCuenta(banco, peticion);
                    case CONSULTAR_SALDO:
                        return manejarConsultarSaldo(banco, peticion);
                    case RETIRAR:
                        return manejarRetirar(banco, peticion);
                    case DEPOSITAR:
                        return manejarDepositar(banco, peticion);
                    case TRANSFERIR:
                        return manejarTransferir(banco, peticion);
                    case HISTORIAL:
                        return manejarHistorial(banco, peticion);
                    default:
                        return Respuesta.error("Operacion no reconocida.");
                }
            } catch (PinInvalidoException | CuentaBloqueadaException
                     | CuentaNoEncontradaException | SaldoInsuficienteException e) {
                return Respuesta.error(e.getMessage());
            } catch (Exception e) {
                return Respuesta.error("Error interno del servidor: " + e.getMessage());
            }
        }

        private Respuesta manejarAutenticar(Banco banco, Peticion p)
                throws PinInvalidoException, CuentaBloqueadaException, CuentaNoEncontradaException {
            Cuenta cuenta = banco.autenticar(p.getNumeroCuenta(), p.getPin());
            return Respuesta.ok("Autenticacion exitosa.")
                .conCuenta(cuenta.getNumeroCuenta(), cuenta.consultarSaldo());
        }

        private Respuesta manejarAbrirCuenta(Banco banco, Peticion p) {
            Cuenta cuenta = banco.abrirCuenta(p.getNombre(), p.getCedula(), "", p.getTelefono(),
                p.getCorreo(), p.getTipoCuenta(), p.getMonto(), p.getPin());
            dao.guardarCliente(p.getNombre(), p.getCedula(), p.getTelefono(), p.getCorreo());
            dao.guardarCuenta(cuenta, p.getCedula());
            return Respuesta.ok("Cuenta creada con exito.")
                .conCuenta(cuenta.getNumeroCuenta(), cuenta.consultarSaldo());
        }

        private Respuesta manejarConsultarSaldo(Banco banco, Peticion p) throws CuentaNoEncontradaException {
            Cuenta cuenta = banco.buscarCuenta(p.getNumeroCuenta());
            return Respuesta.ok("Saldo consultado.")
                .conCuenta(cuenta.getNumeroCuenta(), cuenta.consultarSaldo());
        }

        private Respuesta manejarRetirar(Banco banco, Peticion p)
                throws SaldoInsuficienteException, CuentaNoEncontradaException {
            Cuenta cuenta = banco.buscarCuenta(p.getNumeroCuenta());
            Transaccion t = banco.retirar(cuenta, p.getMonto());
            dao.guardarTransaccion(t);
            dao.actualizarSaldo(cuenta);
            return Respuesta.ok(t.generarComprobante().generar())
                .conCuenta(cuenta.getNumeroCuenta(), cuenta.consultarSaldo());
        }

        private Respuesta manejarDepositar(Banco banco, Peticion p) throws CuentaNoEncontradaException {
            Cuenta cuenta = banco.buscarCuenta(p.getNumeroCuenta());
            Transaccion t = banco.depositar(cuenta, p.getMonto(), p.getTipoDeposito());
            dao.guardarTransaccion(t);
            dao.actualizarSaldo(cuenta);
            return Respuesta.ok(t.generarComprobante().generar())
                .conCuenta(cuenta.getNumeroCuenta(), cuenta.consultarSaldo());
        }

        private Respuesta manejarTransferir(Banco banco, Peticion p)
                throws SaldoInsuficienteException, CuentaNoEncontradaException {
            Cuenta origen = banco.buscarCuenta(p.getNumeroCuenta());
            Transaccion t = banco.transferir(origen, p.getCuentaDestino(), p.getMonto());
            dao.guardarTransaccion(t);
            dao.actualizarSaldo(origen);
            dao.actualizarSaldo(banco.buscarCuentaSilenciosa(p.getCuentaDestino()));
            return Respuesta.ok(t.generarComprobante().generar())
                .conCuenta(origen.getNumeroCuenta(), origen.consultarSaldo());
        }

        private Respuesta manejarHistorial(Banco banco, Peticion p) throws CuentaNoEncontradaException {
            Cuenta cuenta = banco.buscarCuenta(p.getNumeroCuenta());
            List<String> historial = new ArrayList<>();
            for (Transaccion t : banco.obtenerHistorial(cuenta)) {
                historial.add(t.toString());
            }
            return Respuesta.ok("Historial obtenido.").conHistorial(historial);
        }
    }
}

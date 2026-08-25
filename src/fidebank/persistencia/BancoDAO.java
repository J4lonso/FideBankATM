package fidebank.persistencia;

import fidebank.modelo.Cliente;
import fidebank.modelo.Cuenta;
import fidebank.modelo.Transaccion;
import fidebank.modelo.Transferencia;
import fidebank.servicio.Banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Acceso a datos (JDBC puro, sin frameworks/ORM) para persistir clientes, cuentas y
 * transacciones en MySQL. Es la evidencia de "Bases de Datos" pedida en la presentacion final:
 * el servidor deja de depender solo de memoria/serializacion y usa una base de datos real.
 */
public class BancoDAO {

    /** Crea la base de datos (si el driver lo permite) y las tablas necesarias. */
    public void crearTablasSiNoExisten() {
        String clientes =
            "CREATE TABLE IF NOT EXISTS clientes (" +
            "  cedula VARCHAR(20) PRIMARY KEY," +
            "  id_cliente INT NOT NULL," +
            "  nombre VARCHAR(120) NOT NULL," +
            "  telefono VARCHAR(30)," +
            "  correo VARCHAR(120)" +
            ")";
        String cuentas =
            "CREATE TABLE IF NOT EXISTS cuentas (" +
            "  numero_cuenta VARCHAR(20) PRIMARY KEY," +
            "  cedula_cliente VARCHAR(20) NOT NULL," +
            "  tipo_cuenta VARCHAR(20) NOT NULL," +
            "  saldo DOUBLE NOT NULL," +
            "  pin VARCHAR(10) NOT NULL," +
            "  estado VARCHAR(20) NOT NULL," +
            "  FOREIGN KEY (cedula_cliente) REFERENCES clientes(cedula)" +
            ")";
        String transacciones =
            "CREATE TABLE IF NOT EXISTS transacciones (" +
            "  id_transaccion INT PRIMARY KEY," +
            "  numero_cuenta VARCHAR(20) NOT NULL," +
            "  tipo VARCHAR(30) NOT NULL," +
            "  monto DOUBLE NOT NULL," +
            "  fecha DATETIME NOT NULL," +
            "  descripcion VARCHAR(200)," +
            "  cuenta_destino VARCHAR(20)," +
            "  FOREIGN KEY (numero_cuenta) REFERENCES cuentas(numero_cuenta)" +
            ")";

        try (Connection con = ConexionMySQL.obtenerConexion(); Statement st = con.createStatement()) {
            st.execute(clientes);
            st.execute(cuentas);
            st.execute(transacciones);
        } catch (SQLException e) {
            System.err.println("No se pudieron crear las tablas en MySQL: " + e.getMessage());
        }
    }

    public void guardarCliente(String nombre, String cedula, String telefono, String correo) {
        String sql = "INSERT INTO clientes (cedula, id_cliente, nombre, telefono, correo) " +
            "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE nombre = VALUES(nombre)";
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setInt(2, Math.abs(cedula.hashCode()));
            ps.setString(3, nombre);
            ps.setString(4, telefono);
            ps.setString(5, correo);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error guardando cliente en MySQL: " + e.getMessage());
        }
    }

    public void guardarCuenta(Cuenta cuenta, String cedulaCliente) {
        String sql = "INSERT INTO cuentas (numero_cuenta, cedula_cliente, tipo_cuenta, saldo, pin, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE saldo = VALUES(saldo), estado = VALUES(estado)";
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cuenta.getNumeroCuenta());
            ps.setString(2, cedulaCliente);
            ps.setString(3, cuenta.getTipoCuenta());
            ps.setDouble(4, cuenta.consultarSaldo());
            ps.setString(5, cuenta.getPin());
            ps.setString(6, cuenta.getEstado());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error guardando cuenta en MySQL: " + e.getMessage());
        }
    }

    public void actualizarSaldo(Cuenta cuenta) {
        if (cuenta == null) {
            return;
        }
        String sql = "UPDATE cuentas SET saldo = ?, estado = ? WHERE numero_cuenta = ?";
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, cuenta.consultarSaldo());
            ps.setString(2, cuenta.getEstado());
            ps.setString(3, cuenta.getNumeroCuenta());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error actualizando saldo en MySQL: " + e.getMessage());
        }
    }

    /** Inserta la transaccion en la tabla de auditoria (se guarda TODO retiro/deposito/transferencia). */
    public void guardarTransaccion(Transaccion t) {
        String sql = "INSERT INTO transacciones " +
            "(id_transaccion, numero_cuenta, tipo, monto, fecha, descripcion, cuenta_destino) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, t.getIdTransaccion());
            ps.setString(2, t.getNumeroCuenta());
            ps.setString(3, t.getClass().getSimpleName());
            ps.setDouble(4, t.getMonto());
            ps.setTimestamp(5, Timestamp.valueOf(t.getFecha()));
            ps.setString(6, t.getDescripcion());
            ps.setString(7, t instanceof Transferencia ? ((Transferencia) t).getCuentaDestino() : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error guardando transaccion en MySQL: " + e.getMessage());
        }
    }

    /**
     * Carga todos los clientes y cuentas existentes en MySQL hacia el Banco en memoria.
     * Se ejecuta al iniciar el servidor para que los saldos sobrevivan a un reinicio.
     * El historial detallado de transacciones queda disponible en la tabla `transacciones`
     * para auditoria, aunque no se reconstruyen los objetos Transaccion en memoria al arrancar.
     */
    public void cargarEnMemoria(Banco banco) {
        String sql =
            "SELECT c.cedula, c.id_cliente, c.nombre, c.telefono, c.correo, " +
            "       q.numero_cuenta, q.tipo_cuenta, q.saldo, q.pin, q.estado " +
            "FROM clientes c JOIN cuentas q ON q.cedula_cliente = c.cedula";

        try (Connection con = ConexionMySQL.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int cargadas = 0;
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("id_cliente"), rs.getString("nombre"), rs.getString("cedula"),
                    "", rs.getString("telefono"), rs.getString("correo"));

                Cuenta cuenta = new Cuenta(
                    rs.getString("numero_cuenta"), rs.getString("tipo_cuenta"),
                    rs.getDouble("saldo"), rs.getString("pin"));

                banco.registrarDesdePersistencia(cliente, cuenta);
                cargadas++;
            }
            System.out.println("Cuentas cargadas desde MySQL: " + cargadas);
        } catch (SQLException e) {
            System.err.println("No se pudo cargar el estado desde MySQL (se inicia vacio): " + e.getMessage());
        }
    }
}

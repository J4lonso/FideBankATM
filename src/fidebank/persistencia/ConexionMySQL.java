package fidebank.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto unico de conexion a la base de datos MySQL del banco.
 *
 * Ajuste HOST, PUERTO, BASE_DATOS, USUARIO y PASSWORD segun su instalacion local de MySQL
 * antes de correr el servidor. La base de datos y las tablas se crean solas la primera vez
 * (ver BancoDAO.crearTablasSiNoExisten()); solo debe existir el servidor MySQL corriendo.
 */
public class ConexionMySQL {

    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "fidebank";
    private static final String USUARIO = "root";
    private static final String PASSWORD = ""; // coloque aqui su contrasena de MySQL

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}

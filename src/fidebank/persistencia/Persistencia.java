package fidebank.persistencia;

import fidebank.servicio.Banco;

import java.io.*;

/**
 * Persiste el estado del banco (clientes, cuentas, historial de transacciones)
 * usando serializacion de objetos Java (opcional segun la consigna del Avance 2).
 */
public class Persistencia {

    private static final String ARCHIVO = "banco.dat";

    /** Guarda el estado actual del banco en disco. */
    public static void guardar(Banco banco) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(banco);
        } catch (IOException e) {
            System.err.println("Error al guardar el estado del banco: " + e.getMessage());
        }
    }

    /**
     * Carga el banco desde disco si existe un archivo previo; de lo contrario,
     * devuelve la instancia por defecto (vacia) de Banco.
     */
    public static Banco cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return Banco.getInstancia();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Banco banco = (Banco) ois.readObject();
            Banco.setInstancia(banco);
            return banco;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el estado del banco, se inicia uno nuevo: " + e.getMessage());
            return Banco.getInstancia();
        }
    }
}

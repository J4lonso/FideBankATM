package fidebank.red;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Envoltorio del Socket usado por la interfaz grafica del cajero para hablar con el
 * servidor FideBank. Cada instancia mantiene una conexion abierta y sincroniza el
 * envio/recepcion para que la GUI pueda invocar operaciones de forma sencilla,
 * como si fueran metodos locales, aunque en realidad viajan por la red.
 */
public class ClienteRed implements AutoCloseable {

    private final Socket socket;
    private final ObjectOutputStream salida;
    private final ObjectInputStream entrada;

    public ClienteRed(String host, int puerto) throws IOException {
        this.socket = new Socket(host, puerto);
        this.salida = new ObjectOutputStream(socket.getOutputStream());
        this.entrada = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized Respuesta enviar(Peticion peticion) throws IOException {
        salida.writeObject(peticion);
        salida.flush();
        try {
            Object obj = entrada.readObject();
            return (Respuesta) obj;
        } catch (ClassNotFoundException e) {
            throw new IOException("Respuesta invalida del servidor.", e);
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignorado) {
        }
    }
}

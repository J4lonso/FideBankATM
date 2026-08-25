package fidebank.servicio;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Simula la impresora fisica del cajero automatico (HU-07).
 * Corre del lado del cliente (cada cajero tiene su propia impresora fisica).
 * Usa un hilo dedicado (multihilos, opcional) que consume una cola de textos ya
 * formateados (el comprobante que arma el servidor) y los "imprime" (escribe en
 * comprobantes.log) sin bloquear la interfaz grafica.
 */
public class Impresora {

    private static Impresora instancia;

    private final BlockingQueue<String> cola;
    private final Thread hiloImpresion;
    private volatile boolean activa;

    private Impresora() {
        cola = new LinkedBlockingQueue<>();
        activa = true;
        hiloImpresion = new Thread(this::procesarCola, "Hilo-Impresora-FideBank");
        hiloImpresion.setDaemon(true);
        hiloImpresion.start();
    }

    public static synchronized Impresora getInstancia() {
        if (instancia == null) {
            instancia = new Impresora();
        }
        return instancia;
    }

    /** Encola el texto de un comprobante (ya formateado) para ser impreso de forma asincrona. */
    public void encolar(String textoComprobante) {
        cola.offer(textoComprobante);
    }

    private void procesarCola() {
        while (activa) {
            try {
                String texto = cola.take();
                imprimirEnArchivo(texto);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized void imprimirEnArchivo(String texto) {
        try (PrintWriter salida = new PrintWriter(new FileWriter("comprobantes.log", true))) {
            salida.println(texto);
            salida.println();
        } catch (IOException e) {
            System.err.println("No se pudo imprimir el comprobante: " + e.getMessage());
        }
    }

    public void detener() {
        activa = false;
        hiloImpresion.interrupt();
    }
}
